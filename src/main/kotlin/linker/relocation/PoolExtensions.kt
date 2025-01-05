package dev.fir3.wedding.linker.relocation

import dev.fir3.wedding.linker.pool.*

fun Pool.relocateData() {
    var dataIndex = 0u

    for (data in datas) {
        data[RelocatedIndex::class] = RelocatedIndex(dataIndex++)
    }
}

fun Pool.relocateElements() {
    var elementIndex = 0u

    for (element in elements) {
        element[RelocatedIndex::class] = RelocatedIndex(elementIndex++)
    }
}

fun Pool.relocateFunctionTypes() {
    var functionTypeIndex = 0u

    for (functionType in functionTypes) {
        if (functionType[FunctionTypeDuplicate::class] != null) continue
        functionType[RelocatedIndex::class] =
            RelocatedIndex(functionTypeIndex++)
    }
}

fun Pool.relocateFunctions() = relocateImportable(functions)
fun Pool.relocateTables() = relocateImportable(tables)
fun Pool.relocateMemories() = relocateImportable(memories)
fun Pool.relocateGlobals() = relocateImportable(globals)

private fun relocateImportable(importables: List<Object>) {
    val definedImportables = mutableSetOf<Object>()
    var index = 0u

    // Collect definitions and assign new indices to the unresolved imports.

    for (importable in importables) {
        val importResolution = importable[ImportResolution::class]?.index

        if (importResolution != null) {
            continue
        }


        val importModule = importable[AssignedImportModule::class]?.name
            ?: importable[ImportModule::class]?.name

        if (importModule != null) {
            importable[RelocatedIndex::class] = RelocatedIndex(index++)
            continue
        }

        definedImportables += importable
    }

    // Assign indices to definitions

    for (importable in definedImportables) {
        importable[RelocatedIndex::class] = RelocatedIndex(index++)
    }
}
