package dev.fir3.wedding.linker

import dev.fir3.iwan.io.wasm.models.*
import dev.fir3.wedding.NamedModule
import dev.fir3.wedding.input.data.ActiveDataEntry
import dev.fir3.wedding.input.data.DataEntry
import dev.fir3.wedding.input.data.PassiveDataEntry
import dev.fir3.wedding.input.function.ExportedFunction
import dev.fir3.wedding.input.function.FunctionEntry
import dev.fir3.wedding.input.function.ImportedFunction
import dev.fir3.wedding.input.function.InternalFunction
import dev.fir3.wedding.input.global.ExportedGlobal
import dev.fir3.wedding.input.global.GlobalEntry
import dev.fir3.wedding.input.global.ImportedGlobal
import dev.fir3.wedding.input.global.InternalGlobal
import dev.fir3.wedding.input.memory.ExportedMemory
import dev.fir3.wedding.input.memory.ImportedMemory
import dev.fir3.wedding.input.memory.InternalMemory
import dev.fir3.wedding.input.memory.MemoryEntry
import dev.fir3.wedding.linker.function.LinkedExportedFunction
import dev.fir3.wedding.linker.function.LinkedFunctionEntry
import dev.fir3.wedding.linker.function.LinkedImportedFunction
import dev.fir3.wedding.linker.function.LinkedInternalFunction

internal object Linker {
    private const val LINKED_MODULE_NAME = "LINKED"

    fun link(vararg modules: NamedModule): Module {
        val inputDatas = mutableSetOf<DataEntry>()
        val inputFunctions = mutableSetOf<FunctionEntry>()
        val inputGlobals = mutableSetOf<GlobalEntry>()
        val inputMemories = mutableSetOf<MemoryEntry>()

        // Load the location information

        val moduleCollection = modules.toSet()
        loadImports(
            moduleCollection,
            inputFunctions,
            inputGlobals,
            inputMemories
        )

        loadDefinedFunctions(moduleCollection, inputFunctions)
        loadDefinedGlobals(moduleCollection, inputGlobals)
        loadDefinedMemory(moduleCollection, inputMemories)
        loadData(moduleCollection, inputDatas)

        // Relocation tables

        val functionRelocations = RelocationTable()
        val globalRelocations = RelocationTable()
        val memoryRelocations = RelocationTable()

        // Link exports and imports

        var outputFunctions = mutableSetOf<LinkedFunctionEntry>()
        val outputGlobals = mutableSetOf<GlobalEntry>()
        val outputMemories = mutableSetOf<MemoryEntry>()

        globalLinker.link(
            globalRelocations,
            inputGlobals,
            outputGlobals,
            LINKED_MODULE_NAME
        )

        memoryLinker.link(
            memoryRelocations,
            inputMemories,
            outputMemories,
            LINKED_MODULE_NAME
        )

        functionLinker.link(
            functionRelocations,
            inputFunctions,
            outputFunctions,
            LINKED_MODULE_NAME
        )

        outputFunctions = outputFunctions.map { f ->
            when (f) {
                is LinkedExportedFunction -> f.copy(
                    expression = linkExpression(
                        f.expression,
                        f.originalModule,
                        functionRelocations,
                        globalRelocations
                    )
                )

                is LinkedImportedFunction -> f
                is LinkedInternalFunction -> f.copy(
                    expression = linkExpression(
                        f.expression,
                        f.originalModule,
                        functionRelocations,
                        globalRelocations
                    )
                )
            }
        }.toMutableSet()

        val codes = mutableListOf<Code>()
        val datas = mutableListOf<Data>()
        val exports = mutableListOf<Export>()
        val functions = mutableListOf<UInt>()
        val globals = mutableListOf<Global>()
        val imports = mutableListOf<Import>()
        val memories = mutableListOf<MemoryType>()
        val types = collectFunctionTypes(outputFunctions)

        outputFunctions.sortedBy { f -> f.index }.forEach { function ->
            when (function) {
                is LinkedExportedFunction -> {
                    functions += types.indexOf(function.type).toUInt()
                    codes += Code(function.locals, function.expression)
                    exports += FunctionExport(
                        function.functionName,
                        function.index
                    )
                }

                is LinkedImportedFunction -> imports += FunctionImport(
                    function.sourceModule,
                    function.functionName,
                    types.indexOf(function.type).toUInt()
                )

                is LinkedInternalFunction -> {
                    functions += types.indexOf(function.type).toUInt()
                    codes += Code(function.locals, function.expression)
                }
            }
        }

        outputGlobals.sortedBy { global -> global.index }.forEach { global ->
            when (global) {
                is ExportedGlobal -> {
                    exports += GlobalExport(global.globalName, global.index)
                    globals += Global(global.type, global.initializer)
                }

                is ImportedGlobal -> imports += GlobalImport(
                    global.sourceModule,
                    global.globalName,
                    global.type
                )

                is InternalGlobal -> globals += Global(
                    global.type,
                    global.initializer
                )
            }
        }

        outputMemories.sortedBy { m -> m.index }.forEach {  m ->
            when (m) {
                is ExportedMemory -> TODO()
                is ImportedMemory -> imports += MemoryImport(
                    m.sourceModule,
                    m.memoryName,
                    m.type
                )
                is InternalMemory -> TODO()
            }
        }

        datas.addAll(
            inputDatas.map { d ->
                when (d) {
                    is ActiveDataEntry -> ActiveData(
                        initializers = d.initializers,
                        memoryIndex = memoryRelocations
                            .resolveEntry(d.moduleName, d.memoryIndex),
                        linkExpression(
                            d.offset,
                            d.moduleName,
                            functionRelocations,
                            globalRelocations
                        )
                    )
                    is PassiveDataEntry -> PassiveData(
                        d.initializers
                    )
                }
            }
        )

        val startFunctions = outputFunctions.filter { f -> f.isStart }

        if (startFunctions.size > 1) {
            TODO()
        }

        return Module(
            types = types,
            functions = functions,
            tables = emptyList(),
            memories = memories,
            globals = globals,
            elements = emptyList(),
            data = datas,
            imports = imports,
            exports = exports,
            codes = codes,
            startFunction = startFunctions.map { f -> f.index }.singleOrNull()
        )
    }

    private fun loadData(
        modules: Collection<NamedModule>,
        datas: MutableSet<DataEntry>
    ) {
        for (module in modules) {
            var nextDataIndex = 0u

            for (data in module.module.data) {
                datas += when (data) {
                    is ActiveData -> ActiveDataEntry(
                        index = nextDataIndex++,
                        initializers = data.initializers,
                        memoryIndex = data.memoryIndex,
                        moduleName = module.name,
                        offset = data.offset
                    )
                    is PassiveData -> PassiveDataEntry(
                        index = nextDataIndex++,
                        initializers = data.initializers,
                        moduleName = module.name
                    )
                }
            }
        }
    }

    private fun loadDefinedFunctions(
        modules: Collection<NamedModule>,
        functions: MutableSet<FunctionEntry>
    ) {
        for (module in modules) {
            val exports = module
                .module
                .exports
                .filterIsInstance<FunctionExport>()

            val _functions = module.module.functions.zip(module.module.codes)
            val functionOffset = module
                .module
                .imports
                .filterIsInstance<FunctionImport>()
                .count()
                .toUInt()

            var nextFunctionIndex = 0u

            for ((typeIndex, code) in _functions) {
                val functionIndex = functionOffset + (nextFunctionIndex++)
                val functionExport = exports
                    .singleOrNull { export ->
                        export.functionIndex == functionIndex
                    }

                val functionType = module.module.types[typeIndex.toInt()]
                val isStart = module
                    .module
                    .startFunction
                    ?.let { startFunction -> startFunction == functionIndex }
                    ?: false

                functions += if (functionExport == null) {
                    InternalFunction(
                        expression = code.body,
                        index = functionIndex,
                        isStart = isStart,
                        locals = code.locals,
                        moduleName = module.name,
                        type = functionType
                    )
                } else {
                    ExportedFunction(
                        expression = code.body,
                        functionName = functionExport.name,
                        index = functionIndex,
                        isStart = isStart,
                        locals = code.locals,
                        moduleName = module.name,
                        type = functionType
                    )
                }
            }
        }
    }

    private fun loadDefinedGlobals(
        modules: Collection<NamedModule>,
        globals: MutableSet<GlobalEntry>
    ) {
        for (module in modules) {
            val exports = module
                .module
                .exports
                .filterIsInstance<GlobalExport>()

            val globalOffset = module
                .module
                .imports
                .filterIsInstance<GlobalImport>()
                .count()
                .toUInt()

            var nextGlobalIndex = 0u

            for (global in module.module.globals) {
                val globalIndex = globalOffset + (nextGlobalIndex++)
                val globalExport = exports
                    .singleOrNull { export ->
                        export.globalIndex == globalIndex
                    }

                globals += if (globalExport == null) {
                    InternalGlobal(
                        index = globalIndex,
                        initializer = global.initializer,
                        moduleName = module.name,
                        type = global.type
                    )
                } else {
                    ExportedGlobal(
                        globalName = globalExport.name,
                        index = globalIndex,
                        initializer = global.initializer,
                        moduleName = module.name,
                        type = global.type
                    )
                }
            }
        }
    }

    private fun loadDefinedMemory(
        modules: Collection<NamedModule>,
        memories: MutableSet<MemoryEntry>
    ) {
        for (module in modules) {
            val exports = module
                .module
                .exports
                .filterIsInstance<MemoryExport>()

            val memoryOffset = module
                .module
                .memories
                .filterIsInstance<MemoryImport>()
                .count()
                .toUInt()

            var nextMemoryIndex = 0u

            for (memoryType in module.module.memories) {
                val memoryIndex = memoryOffset + (nextMemoryIndex++)
                val memoryExport = exports
                    .singleOrNull { export ->
                        export.memoryIndex == memoryIndex
                    }

                memories += if (memoryExport == null) {
                    InternalMemory(
                        index = memoryIndex,
                        moduleName = module.name,
                        type = memoryType
                    )
                } else {
                    ExportedMemory(
                        index = memoryIndex,
                        memoryName = memoryExport.name,
                        moduleName = module.name,
                        type = memoryType
                    )
                }
            }
        }
    }

    private fun loadImports(
        modules: Collection<NamedModule>,
        functions: MutableSet<FunctionEntry>,
        globals: MutableSet<GlobalEntry>,
        memories: MutableSet<MemoryEntry>
    ) {
        for (module in modules) {
            var nextFunctionIndex = 0u
            var nextGlobalIndex = 0u
            var nextMemoryIndex = 0u

            for (import in module.module.imports) {
                when (import) {
                    is FunctionImport -> {
                        val functionIndex = nextFunctionIndex++
                        val isStart = module
                            .module
                            .startFunction
                            ?.let { startFunction ->
                                startFunction == functionIndex
                            }
                            ?: false

                        functions += ImportedFunction(
                            index = functionIndex,
                            isStart = isStart,
                            moduleName = module.name,
                            sourceModule = import.module,
                            functionName = import.name,
                            type = module.module.types[
                                import.typeIndex.toInt()
                            ]
                        )
                    }
                    is GlobalImport -> globals += ImportedGlobal(
                        globalName = import.name,
                        index = nextGlobalIndex++,
                        moduleName = module.name,
                        sourceModule = import.module,
                        type = import.type
                    )
                    is MemoryImport -> memories += ImportedMemory(
                        index = nextMemoryIndex++,
                        memoryName = import.name,
                        moduleName = module.name,
                        sourceModule = import.module,
                        type = import.type
                    )
                    is TableImport -> TODO()
                }
            }
        }
    }
}
