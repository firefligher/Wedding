package dev.fir3.wedding.linker.sanity

import dev.fir3.wedding.linker.pool.*

fun Pool.checkForDuplicateExports(): Set<DuplicateExport> {
    val exports = mutableMapOf<String, Object>()
    val exportables = functions
        .union(globals)
        .union(memories)
        .union(tables)

    val duplicates = mutableMapOf<String, MutableSet<Object>>()

    for (exportable in exportables) {
        val names = exportable[AssignedName::class]
            ?.name?.let(::setOf)
            ?: exportable[SourceNames::class]?.names

        names?.forEach { name ->
            val removedObject = exports.put(name, exportable)
            if (removedObject != null) {
                duplicates
                    .computeIfAbsent(name) { mutableSetOf(removedObject) }
                    .add(exportable)
            }
        }
    }

    return duplicates.map { (name, objects) ->
        DuplicateExport(name, objects)
    }.toSet()
}
