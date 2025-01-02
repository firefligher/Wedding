package dev.fir3.wedding.linking

import dev.fir3.iwan.io.sink.OutputStreamByteSink
import dev.fir3.iwan.io.source.InputStreamByteSource
import dev.fir3.iwan.io.wasm.BinaryFormat
import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.FunctionType
import dev.fir3.iwan.io.wasm.models.Module
import dev.fir3.iwan.io.wasm.models.instructions.*
import dev.fir3.iwan.io.wasm.models.valueTypes.NumberType
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType
import dev.fir3.iwan.io.wasm.models.valueTypes.VectorType
import dev.fir3.wedding.input.loader.*
import dev.fir3.wedding.input.model.MutableInputContainer
import dev.fir3.wedding.input.model.RenameEntry
import dev.fir3.wedding.input.model.function.DefinedUnlinkedFunction
import dev.fir3.wedding.input.model.global.DefinedUnlinkedGlobal
import dev.fir3.wedding.input.model.identifier.ExportedGlobalIdentifier
import dev.fir3.wedding.input.model.identifier.GlobalIdentifier
import dev.fir3.wedding.input.model.identifier.Identifier
import dev.fir3.wedding.input.model.identifier.ImportedGlobalIdentifier
import dev.fir3.wedding.linking.model.MutableRelocationContainer
import dev.fir3.wedding.linking.model.NamedModule
import dev.fir3.wedding.linking.relocator.*
import dev.fir3.wedding.linking.renamer.*
import dev.fir3.wedding.output.collector.*
import dev.fir3.wedding.output.model.MutableOutputContainer
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

internal class LinkingExecutor : AbstractExecutor() {
    companion object {
        private const val OUTPUT_MODULE_NAME = "LINKED"
    }

    override fun execute(
        inputModulePaths: Collection<Pair<String, Path>>,
        outputModulePath: Path?,
        renameEntries: Collection<RenameEntry<*>>,
        definedGlobals: Collection<ExportedGlobalIdentifier>,
        wrappedGlobals: Collection<GlobalIdentifier>
    ) {
        // Deserialize the WebAssembly modules

        val inputModules = inputModulePaths.map { (name, path) ->
            try {
                val module = Files
                    .newInputStream(
                        path,
                        StandardOpenOption.READ
                    )
                    .let(::InputStreamByteSource)
                    .use(BinaryFormat::deserializeModule)

                NamedModule(name, module)
            } catch (ex: IOException) {
                ex.printStackTrace()
                TODO("We should implement error handling here")
            }
        }

        // Merge all significant information into a large source container,
        // that we then consider as immutable.

        val inputContainer = MutableInputContainer()

        DataLoader.load(inputModules, inputContainer.datas)
        ElementLoader.load(inputModules, inputContainer.elements)
        FunctionLoader.load(inputModules, inputContainer.functions)
        GlobalLoader.load(inputModules, inputContainer.globals)
        MemoryLoader.load(inputModules, inputContainer.memories)
        TableLoader.load(inputModules, inputContainer.tables)

        // Apply renaming.

        ExportedFunctionRenamer.apply(inputContainer, renameEntries)
        ExportedGlobalRenamer.apply(inputContainer, renameEntries)
        ExportedMemoryRenamer.apply(inputContainer, renameEntries)
        ImportedFunctionRenamer.apply(inputContainer, renameEntries)
        ImportedGlobalRenamer.apply(inputContainer, renameEntries)
        ImportedMemoryRenamer.apply(inputContainer, renameEntries)

        // Define globals and global wrappers

        definedGlobals.forEach { global ->
            val index = inputContainer.globals.size.toUInt()

            inputContainer.globals += DefinedUnlinkedGlobal(
                exportName = global.global,
                index = index,
                initializer = when (global.type.valueType) {
                    NumberType.Float32 -> Expression(listOf(Float32ConstInstruction(0.0F)))
                    NumberType.Float64 -> Expression(listOf(Float64ConstInstruction(0.0)))
                    NumberType.Int32 -> Expression(listOf(Int32ConstInstruction(0)))
                    NumberType.Int64 -> Expression(listOf(Int64ConstInstruction(0)))
                    ReferenceType.ExternalReference -> TODO()
                    ReferenceType.FunctionReference -> TODO()
                    VectorType.Vector128 -> TODO()
                },
                module = global.module,
                type = global.type
            )

            if (global.type.isMutable) {
                inputContainer.functions += DefinedUnlinkedFunction(
                    exportName = global.global + "_SET",
                    expression = Expression(
                        listOf(
                            LocalGetInstruction(0u),
                            GlobalSetInstruction(index)
                        )
                    ),
                    index = inputContainer.functions.size.toUInt(),
                    isStart = false,
                    locals = emptyList(),
                    module = global.module,
                    type = FunctionType(
                        parameterTypes = listOf(global.type.valueType),
                        resultTypes = emptyList()
                    )
                )
            }

            inputContainer.functions += DefinedUnlinkedFunction(
                exportName = global.global + "_GET",
                expression = Expression(
                    listOf(GlobalGetInstruction(index))
                ),
                index = inputContainer.functions.size.toUInt(),
                isStart = false,
                locals = emptyList(),
                module = global.module,
                type = FunctionType(
                    parameterTypes = emptyList(),
                    resultTypes = listOf(global.type.valueType)
                )
            )
        }

        wrappedGlobals.forEach { global ->
            val (module, name) = when (global) {
                is ExportedGlobalIdentifier -> Pair(global.module, global.global)
                is ImportedGlobalIdentifier -> Pair(global.sourceModule, global.global)
            }

            val index = inputContainer.globals.single { entry ->
                entry.exportName == name && entry.module == module
            }.index

            if (global.type.isMutable) {
                inputContainer.functions += DefinedUnlinkedFunction(
                    exportName = name + "_SET",
                    expression = Expression(
                        listOf(
                            LocalGetInstruction(0u),
                            GlobalSetInstruction(index)
                        )
                    ),
                    index = inputContainer.functions.size.toUInt(),
                    isStart = false,
                    locals = emptyList(),
                    module = module,
                    type = FunctionType(
                        parameterTypes = listOf(global.type.valueType),
                        resultTypes = emptyList()
                    )
                )
            }

            inputContainer.functions += DefinedUnlinkedFunction(
                exportName = name + "_GET",
                expression = Expression(
                    listOf(GlobalGetInstruction(index))
                ),
                index = inputContainer.functions.size.toUInt(),
                isStart = false,
                locals = emptyList(),
                module = module,
                type = FunctionType(
                    parameterTypes = emptyList(),
                    resultTypes = listOf(global.type.valueType)
                )
            )
        }

        // Reassign indices and link imports with exports, if possible.

        val relocationContainer = MutableRelocationContainer()

        DataRelocator.relocate(
            relocationContainer.dataRelocations,
            inputContainer.datas,
            relocationContainer.datas,
            OUTPUT_MODULE_NAME
        )

        ElementRelocator.relocate(
            relocationContainer.elementRelocations,
            inputContainer.elements,
            relocationContainer.elements,
            OUTPUT_MODULE_NAME
        )

        FunctionRelocator.relocate(
            relocationContainer.functionRelocations,
            inputContainer.functions,
            relocationContainer.functions,
            OUTPUT_MODULE_NAME
        )

        GlobalRelocator.relocate(
            relocationContainer.globalRelocations,
            inputContainer.globals,
            relocationContainer.globals,
            OUTPUT_MODULE_NAME
        )

        MemoryRelocator.relocate(
            relocationContainer.memoryRelocations,
            inputContainer.memories,
            relocationContainer.memories,
            OUTPUT_MODULE_NAME
        )

        TableRelocator.relocate(
            relocationContainer.tableRelocations,
            inputContainer.tables,
            relocationContainer.tables,
            OUTPUT_MODULE_NAME
        )

        // Fix expressions and their instructions.

        fixInstructions(relocationContainer)

        // Build the output module

        val outputContainer = MutableOutputContainer()

        DataCollector.collect(relocationContainer, outputContainer)
        ElementCollector.collect(relocationContainer, outputContainer)
        FunctionCollector.collect(relocationContainer, outputContainer)
        GlobalCollector.collect(relocationContainer, outputContainer)
        MemoryCollector.collect(relocationContainer, outputContainer)
        TableCollector.collect(relocationContainer, outputContainer)

        val module = Module(
            types = outputContainer.types,
            functions = outputContainer.functions,
            tables = outputContainer.tables,
            memories = outputContainer.memories,
            globals = outputContainer.globals,
            elements = outputContainer.elements,
            data = outputContainer.data,
            imports = outputContainer.imports,
            exports = outputContainer.exports,
            codes = outputContainer.codes,
            startFunction = outputContainer.startFunction
        )

        // Serialize the module

        try {
            Files.newOutputStream(
                requireNotNull(outputModulePath),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            ).let(::OutputStreamByteSink).use { sink ->
                BinaryFormat.serializeModule(sink, module)
            }
        } catch (ex: IOException) {
            TODO()
        }
    }
}

private inline fun <reified TIdentifier : Identifier<*>>
AbstractRenamer<TIdentifier, *>.apply(
    container: MutableInputContainer,
    renameEntries: Collection<RenameEntry<*>>
) = rename(container, renameEntries.filterIsInstance<RenameEntry<TIdentifier>>())
