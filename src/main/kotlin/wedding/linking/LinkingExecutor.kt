package dev.fir3.wedding.linking

import dev.fir3.iwan.io.sink.OutputStreamByteSink
import dev.fir3.iwan.io.source.InputStreamByteSource
import dev.fir3.iwan.io.wasm.BinaryFormat
import dev.fir3.iwan.io.wasm.models.Module
import dev.fir3.wedding.input.loader.*
import dev.fir3.wedding.input.loader.DataLoader
import dev.fir3.wedding.input.loader.FunctionLoader
import dev.fir3.wedding.input.loader.GlobalLoader
import dev.fir3.wedding.input.loader.MemoryLoader
import dev.fir3.wedding.input.model.MutableInputContainer
import dev.fir3.wedding.input.model.RenameEntry
import dev.fir3.wedding.input.model.identifier.Identifier
import dev.fir3.wedding.linking.model.MutableRelocationContainer
import dev.fir3.wedding.linking.model.NamedModule
import dev.fir3.wedding.linking.relocator.*
import dev.fir3.wedding.linking.relocator.DataRelocator
import dev.fir3.wedding.linking.relocator.FunctionRelocator
import dev.fir3.wedding.linking.relocator.GlobalRelocator
import dev.fir3.wedding.linking.relocator.MemoryRelocator
import dev.fir3.wedding.linking.renamer.*
import dev.fir3.wedding.linking.renamer.AbstractRenamer
import dev.fir3.wedding.linking.renamer.ExportedFunctionRenamer
import dev.fir3.wedding.linking.renamer.ExportedGlobalRenamer
import dev.fir3.wedding.linking.renamer.ExportedMemoryRenamer
import dev.fir3.wedding.output.collector.*
import dev.fir3.wedding.output.collector.DataCollector
import dev.fir3.wedding.output.collector.FunctionCollector
import dev.fir3.wedding.output.collector.GlobalCollector
import dev.fir3.wedding.output.collector.MemoryCollector
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
        renameEntries: Collection<RenameEntry<*>>
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

        // Reassign indices and link imports with exports, if possible.

        val relocationContainer = MutableRelocationContainer()

        DataRelocator.relocate(
            relocationContainer.dataRelocations,
            inputContainer.datas,
            relocationContainer.datas,
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
