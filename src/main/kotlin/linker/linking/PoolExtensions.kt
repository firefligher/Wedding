package dev.fir3.wedding.linker.linking

import dev.fir3.wedding.linker.pool.*
import dev.fir3.wedding.linker.pool.Function
import dev.fir3.wedding.wasm.CallInstruction
import dev.fir3.wedding.wasm.Code
import java.util.*

fun Pool.link(): Set<Conflict> {
    val exports = mutableMapOf<Pair<String, String>, Object>()
    val imports = mutableMapOf<Pair<String, String>, MutableSet<Object>>()
    val conflicts = mutableSetOf<Conflict>()

    // Collect imports and exports

    val objects = functions + tables + memories + globals

    for (`object` in objects) {
        val assignedNames = `object`[AssignedName::class]
            ?.name?.let(::setOf)
            ?: `object`[SourceNames::class]?.names

        assignedNames?.forEach { assignedName ->
            val sourceModule = `object`[SourceModule::class]!!.name
            exports[Pair(sourceModule, assignedName)] = `object`
        }

        val importModule = `object`[AssignedImportModule::class]?.name
            ?: `object`[ImportModule::class]?.let(ImportModule::name)

        val importName = `object`[AssignedImportName::class]?.name
            ?: `object`[ImportName::class]?.let(ImportName::name)

        if (importModule != null && importName != null) {
            imports.computeIfAbsent(Pair(importModule, importName)) { _ ->
                mutableSetOf()
            } += `object`
        }
    }

    // Link imports with exports, if possible.

    for ((identifier, importSet) in imports) {
        val export = exports[identifier] ?: continue

        for (import in importSet) {
            if (!import.isImportCompatibleWith(export, this)) {
                conflicts += Conflict(
                    export = export.identifier,
                    import = import.identifier
                )

                continue
            }

            import[ImportResolution::class] = ImportResolution(
                export[SourceIndex::class]!!.index
            )
        }
    }

    return conflicts
}


fun Pool.linkStartFunctions(syntheticSourceModuleName: String) {
    val startFunctions = mutableSetOf<Function>()

    for (function in functions) {
        if (function[StartFunction::class] != StartFunction) continue
        startFunctions += function
    }

    if (startFunctions.isEmpty()) return
    if (startFunctions.size == 1) {
        val startFunction = startFunctions.single()
        startFunction[FixedStartFunction::class] = FixedStartFunction
        return
    }

    // Ensure that all start functions are exported.

    val startFunctionCoordinates = startFunctions.map { function ->
        val module = function[SourceModule::class]!!.name
        val names = function[AssignedName::class]?.name?.let(::setOf)
            ?: function[SourceNames::class]?.names

        var name = names?.first()

        if (name == null) {
            name = UUID.randomUUID().toString()
            function[AssignedName::class] = AssignedName(name)
        }

        Pair(module, name)
    }

    // Generate a new module with a function that invokes all the other start
    // functions.

    add(
        FunctionType(
            mutableMapOf(
                SourceModule::class to SourceModule(syntheticSourceModuleName),
                SourceIndex::class to SourceIndex(0u),
                FunctionTypeInfo::class to FunctionTypeInfo(
                    dev.fir3.wedding.wasm.FunctionType(emptyList(), emptyList())
                )
            )
        )
    )

    val startFunctionIndices = startFunctionCoordinates
        .mapIndexed { index, (module, name) ->
            val functionImport = Function(
                mutableMapOf(
                    SourceModule::class to
                            SourceModule(syntheticSourceModuleName),

                    ImportModule::class to ImportModule(module),
                    ImportName::class to ImportName(name),
                    SourceIndex::class to SourceIndex(index.toUInt()),
                    FunctionTypeIndex::class to FunctionTypeIndex(0u)
                )
            )

            add(functionImport)
            index
        }

    val instructions = startFunctionIndices.map { index ->
        CallInstruction(index.toUInt())
    }

    add(
        Function(
            mutableMapOf(
                SourceModule::class to
                        SourceModule(syntheticSourceModuleName),

                SourceIndex::class to
                        SourceIndex(startFunctionIndices.size.toUInt()),

                FunctionTypeIndex::class to FunctionTypeIndex(0u),
                FunctionBody::class to
                        FunctionBody(Code(instructions, emptyList())),

                FixedStartFunction::class to FixedStartFunction
            )
        )
    )
}
