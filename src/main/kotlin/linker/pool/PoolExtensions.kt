package dev.fir3.wedding.linker.pool

import dev.fir3.wedding.wasm.*
import dev.fir3.wedding.wasm.DeclarativeElement

operator fun Pool.plusAssign(`object`: Object) = add(`object`)
operator fun Pool.minusAssign(`object`: Object) = remove(`object`)

fun Pool.add(name: String, module: Module) {
    val sourceModule = SourceModule(name)

    module.types.forEachIndexed { index, type ->
        this += FunctionType(
            mutableMapOf(
                SourceModule::class to sourceModule,
                SourceIndex::class to SourceIndex(index.toUInt()),
                FunctionTypeInfo::class to FunctionTypeInfo(type)
            )
        )
    }

    // Imports

    val functions = mutableListOf<Function>()
    val tables = mutableListOf<Table>()
    val memories = mutableListOf<Memory>()
    val globals = mutableListOf<Global>()

    for (import in module.imports) {
        val annotations = mutableMapOf(
            SourceModule::class to sourceModule,
            ImportModule::class to ImportModule(import.module),
            ImportName::class to ImportName(import.name),
        )

        when (import) {
            is FunctionImport -> {
                annotations[SourceIndex::class] = SourceIndex(
                    functions.size.toUInt()
                )

                annotations[FunctionTypeIndex::class] = FunctionTypeIndex(
                    import.typeIndex
                )

                functions += Function(annotations)
            }

            is GlobalImport -> {
                annotations[SourceIndex::class] = SourceIndex(
                    globals.size.toUInt()
                )

                annotations[GlobalType::class] = GlobalType(
                    isMutable = import.isMutable,
                    type = import.type
                )

                globals += Global(annotations)
            }

            is MemoryImport -> {
                annotations[SourceIndex::class] = SourceIndex(
                    memories.size.toUInt()
                )

                annotations[MemoryInfo::class] = MemoryInfo(
                    limits = import.limits
                )

                memories += Memory(annotations)
            }

            is TableImport -> {
                annotations[SourceIndex::class] = SourceIndex(
                    tables.size.toUInt()
                )

                annotations[TableInfo::class] = TableInfo(import.type)
                tables += Table(annotations)
            }
        }
    }

    val importFunctionCount = functions.size.toUInt()

    // Functions

    for (typeIndex in module.functions) {
        functions += Function(
            mutableMapOf(
                SourceModule::class to sourceModule,
                SourceIndex::class to SourceIndex(functions.size.toUInt()),
                FunctionTypeIndex::class to FunctionTypeIndex(typeIndex)
            )
        )
    }

    // Tables

    for (type in module.tables) {
        tables += Table(
            mutableMapOf(
                SourceModule::class to sourceModule,
                SourceIndex::class to SourceIndex(tables.size.toUInt()),
                TableInfo::class to TableInfo(type)
            )
        )
    }

    // Memory

    for (limits in module.memories) {
        memories += Memory(
            mutableMapOf(
                SourceModule::class to sourceModule,
                SourceIndex::class to SourceIndex(memories.size.toUInt()),
                MemoryInfo::class to MemoryInfo(limits)
            )
        )
    }

    // Global

    for (global in module.globals) {
        globals += Global(
            mutableMapOf(
                SourceModule::class to sourceModule,
                SourceIndex::class to SourceIndex(globals.size.toUInt()),
                GlobalInitializer::class to GlobalInitializer(
                    global.initializer
                ),
                GlobalType::class to GlobalType(
                    isMutable = global.isMutable,
                    type = global.type
                )
            )
        )
    }

    // Export

    for (export in module.exports) {
        val annotations = when (export) {
            is FunctionExport ->
                functions[export.functionIndex.toInt()].annotations

            is GlobalExport -> globals[export.globalIndex.toInt()].annotations
            is MemoryExport -> memories[export.memoryIndex.toInt()].annotations
            is TableExport -> tables[export.tableIndex.toInt()].annotations
        }

        annotations[SourceName::class] = SourceName(export.name)
    }

    // Start

    module.startFunctionIndex?.let { index ->
        functions[index.toInt()].annotations[StartFunction::class] =
            StartFunction
    }

    // Elements

    var elementIndex = 0u

    for (element in module.elements) {
        val annotations = mutableMapOf(
            SourceModule::class to sourceModule,
            SourceIndex::class to SourceIndex(elementIndex++),
            ElementInfo::class to ElementInfo(
                initializers = element.initializers,
                type = element.type
            )
        )

        this += when (element) {
            is ActiveElement -> {
                annotations[ActiveElementInfo::class] = ActiveElementInfo(
                    offset = element.offset,
                    tableIndex = element.tableIndex
                )

                Element(annotations)
            }

            is DeclarativeElement -> {
                annotations[
                    dev.fir3.wedding.linker.pool.DeclarativeElement::class
                ] = DeclarativeElement

                Element(annotations)
            }

            is PassiveElement -> Element(annotations)
        }
    }

    // Code

    module.codes.forEachIndexed { index, code ->
        functions[
            (importFunctionCount + index.toUInt()).toInt()
        ].annotations[FunctionBody::class] = FunctionBody(code)
    }

    // Data

    var dataIndex = 0u

    for (data in module.datas) {
        val annotations = mutableMapOf(
            SourceModule::class to sourceModule,
            SourceIndex::class to SourceIndex(dataIndex++),
            DataContent::class to DataContent(data.initializers)
        )

        this += when (data) {
            is ActiveData -> {
                annotations[ActiveDataInfo::class] = ActiveDataInfo(
                    memoryIndex = data.memoryIndex,
                    offset = data.offset
                )

                Data(annotations)
            }
            is PassiveData -> Data(annotations)
        }
    }

    // Add the exportable things to the pool as well.

    functions.forEach(::add)
    tables.forEach(::add)
    memories.forEach(::add)
    globals.forEach(::add)
}

fun Pool.createSourceIndex() = PoolSourceIndex(
    datas = createSourceIndex(datas),
    elements = createSourceIndex(elements),
    functions = createSourceIndex(functions),
    functionTypes = createSourceIndex(functionTypes),
    globals = createSourceIndex(globals),
    memories = createSourceIndex(memories),
    tables = createSourceIndex(tables)
)

private fun <TObject : Object> createSourceIndex(
    objects: Collection<TObject>
): Map<Pair<String, UInt>, TObject> {
    val map = mutableMapOf<Pair<String, UInt>, TObject>()

    for (`object` in objects) {
        val sourceModule = `object`[SourceModule::class]!!.name
        val sourceIndex = `object`[SourceIndex::class]!!.index

        map[Pair(sourceModule, sourceIndex)] = `object`
    }

    return map
}

fun Pool.resolve(identifier: Identifier): Object? {
    val entries = datas
        .union(elements)
        .union(functions)
        .union(functionTypes)
        .union(globals)
        .union(memories)
        .union(tables)

    return entries.singleOrNull { `object` ->
        `object`.identifier == identifier
    }
}

fun Pool.toModule(): Module {
    val codes = mutableMapOf<UInt, Code>()
    val datas = mutableMapOf<UInt, dev.fir3.wedding.wasm.Data>()
    val elements = mutableMapOf<UInt, dev.fir3.wedding.wasm.Element>()
    val exports = mutableListOf<Export>()
    val functions = mutableMapOf<UInt, UInt>()
    val functionImports = mutableMapOf<UInt, FunctionImport>()
    val globals = mutableMapOf<UInt, dev.fir3.wedding.wasm.Global>()
    val globalImports = mutableMapOf<UInt, GlobalImport>()
    val memories = mutableMapOf<UInt, Limits>()
    val memoryImports = mutableMapOf<UInt, MemoryImport>()
    var startFunctionIndex: UInt? = null
    val tables = mutableMapOf<UInt, TableType>()
    val tableImports = mutableMapOf<UInt, TableImport>()
    val types = mutableMapOf<UInt, dev.fir3.wedding.wasm.FunctionType>()

    // Data

    for (data in this.datas) {
        val index = data[RelocatedIndex::class]!!.index
        val content = data[DataContent::class]!!.content
        val activeDataInfo = data[FixedActiveDataInfo::class]

        datas[index] = if (activeDataInfo == null) {
            PassiveData(content)
        } else {
            ActiveData(
                content,
                activeDataInfo.memoryIndex,
                activeDataInfo.offset
            )
        }
    }

    // Elements

    for (element in this.elements) {
        val index = element[RelocatedIndex::class]!!.index
        val declarativeElement = element[
            dev.fir3.wedding.linker.pool.DeclarativeElement::class
        ]

        val activeElementInfo = element[FixedActiveElementInfo::class]
        val type = element[ElementInfo::class]!!.type
        val initializers = element[FixedElementInitializers::class]!!
            .initializers

        elements[index] = if (activeElementInfo != null) {
            ActiveElement(
                initializers,
                activeElementInfo.offset,
                activeElementInfo.tableIndex,
                type
            )
        } else if (declarativeElement != null) {
            DeclarativeElement(initializers, type)
        } else {
            PassiveElement(initializers, type)
        }
    }

    // Functions

    for (function in this.functions) {
        val index = function[RelocatedIndex::class]?.index ?: continue
        val name = function[AssignedName::class]?.name
            ?: function[SourceName::class]?.name

        if (name != null) {
            exports += FunctionExport(index, name)
        }

        val typeIndex = function[FixedFunctionTypeIndex::class]!!.typeIndex
        val code = function[FixedFunctionBody::class]?.code
        val startFunction = function[StartFunction::class]

        if (startFunction != null) {
            startFunctionIndex = index
        }

        if (code != null) {
            functions[index] = typeIndex
            codes[index] = code
            continue
        }

        val importModule = function[AssignedImportModule::class]?.name
            ?: function[ImportModule::class]?.name

        val importName = function[AssignedImportName::class]?.name
            ?: function[ImportName::class]?.name

        functionImports[index] = FunctionImport(
            importModule!!,
            importName!!,
            typeIndex
        )
    }

    // Function Types

    for (functionType in functionTypes) {
        val index = functionType[RelocatedIndex::class]?.index ?: continue
        val type = functionType[FunctionTypeInfo::class]!!.type

        types[index] = type
    }

    // Globals

    for (global in this.globals) {
        val index = global[RelocatedIndex::class]?.index ?: continue
        val name = global[AssignedName::class]?.name
            ?: global[SourceName::class]?.name

        if (name != null) {
            exports += GlobalExport(index, name)
        }

        val (isMutable, type) = global[GlobalType::class]!!
        val initializer = global[FixedGlobalInitializer::class]?.instructions

        if (initializer != null) {
            globals[index] = Global(initializer, isMutable, type)
            continue
        }

        val importModule = global[AssignedImportModule::class]?.name
            ?: global[ImportModule::class]?.name

        val importName = global[AssignedImportName::class]?.name
            ?: global[ImportName::class]?.name

        globalImports[index] = GlobalImport(
            isMutable,
            importModule!!,
            importName!!,
            type
        )
    }

    // Memories

    for (memory in this.memories) {
        val index = memory[RelocatedIndex::class]?.index ?: continue
        val name = memory[AssignedName::class]?.name
            ?: memory[SourceName::class]?.name

        if (name != null) {
            exports += MemoryExport(index, name)
        }

        val (limits) = memory[MemoryInfo::class]!!
        val importModule = memory[AssignedImportModule::class]?.name
            ?: memory[ImportModule::class]?.name

        val importName = memory[AssignedImportName::class]?.name
            ?: memory[ImportName::class]?.name

        if (importModule != null && importName != null) {
            memoryImports[index] = MemoryImport(
                limits,
                importModule,
                importName
            )

            continue
        }

        memories[index] = limits
    }

    // Tables

    for (table in this.tables) {
        val index = table[RelocatedIndex::class]?.index ?: continue
        val name = table[AssignedName::class]?.name
            ?: table[SourceName::class]?.name

        if (name != null) {
            exports += MemoryExport(index, name)
        }

        val (type) = table[TableInfo::class]!!
        val importModule = table[AssignedImportModule::class]?.name
            ?: table[ImportModule::class]?.name

        val importName = table[AssignedImportName::class]?.name
            ?: table[ImportName::class]?.name

        if (importModule != null && importName != null) {
            tableImports[index] = TableImport(
                importModule,
                importName,
                type
            )

            continue
        }

        tables[index] = type
    }

    return Module(
        codes = codes
            .toList()
            .sortedBy { (index, _) -> index }
            .map { (_, code) -> code },

        datas = datas
            .toList()
            .sortedBy { (index, _) -> index }
            .map { (_, data) -> data },

        elements = elements
            .toList()
            .sortedBy { (index, _) -> index }
            .map { (_, element) -> element },

        exports = exports,
        functions = functions
            .toList()
            .sortedBy { (index, _) -> index }
            .map { (_, typeIndex) -> typeIndex },

        globals = globals
            .toList()
            .sortedBy { (index, _) -> index }
            .map { (_, global) -> global },

        imports = (
                functionImports + globalImports + memoryImports + tableImports
        ).toList()
            .sortedBy { (index, _) -> index }
            .map { (_, import) -> import },

        memories = memories
            .toList()
            .sortedBy { (index, _) -> index }
            .map { (_, memory) -> memory },

        startFunctionIndex = startFunctionIndex,
        tables = tables
            .toList()
            .sortedBy { (index, _) -> index }
            .map { (_, table) -> table },

        types = types
            .toList()
            .sortedBy { (index, _) -> index }
            .map { (_, type) -> type },
    )
}
