package dev.fir3.wedding.linker.linking

import dev.fir3.wedding.linker.pool.*

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
