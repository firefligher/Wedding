package dev.fir3.wedding.linker.pool

fun PoolSourceIndex.resolveDataIndex(
    sourceModule: String,
    sourceIndex: UInt
) = datas[Pair(sourceModule, sourceIndex)]!![RelocatedIndex::class]!!.index

fun PoolSourceIndex.resolveElementIndex(
    sourceModule: String,
    sourceIndex: UInt
) = elements[Pair(sourceModule, sourceIndex)]!![RelocatedIndex::class]!!.index

fun PoolSourceIndex.resolveFunctionIndex(
    sourceModule: String,
    sourceIndex: UInt
) = resolveImportable(sourceModule, sourceIndex, functions)

fun PoolSourceIndex.resolveFunctionTypeIndex(
    sourceModule: String,
    sourceIndex: UInt
): UInt {
    val functionType = functionTypes[Pair(sourceModule, sourceIndex)]!!
    val relocatedIndex = functionType[RelocatedIndex::class]?.index

    if (relocatedIndex != null) {
        return relocatedIndex
    }

    val duplicate = functionType[FunctionTypeDuplicate::class]!!

    return resolveFunctionTypeIndex(
        sourceModule = duplicate.module,
        sourceIndex = duplicate.typeIndex
    )
}

fun PoolSourceIndex.resolveGlobalIndex(
    sourceModule: String,
    sourceIndex: UInt
) = resolveImportable(sourceModule, sourceIndex, globals)

fun PoolSourceIndex.resolveMemoryIndex(
    sourceModule: String,
    sourceIndex: UInt
) = resolveImportable(sourceModule, sourceIndex, memories)

fun PoolSourceIndex.resolveTableIndex(
    sourceModule: String,
    sourceIndex: UInt
) = resolveImportable(sourceModule, sourceIndex, tables)

private fun resolveImportable(
    sourceModule: String,
    sourceIndex: UInt,
    objects: Map<Pair<String, UInt>, Object>
): UInt {
    val `object` = objects[Pair(sourceModule, sourceIndex)]!!
    val relocatedIndex = `object`[RelocatedIndex::class]?.index

    if (relocatedIndex != null) {
        return relocatedIndex
    }

    val importModule = `object`[ImportModule::class]!!.name
    val importResolution = `object`[ImportResolution::class]!!.index

    return resolveImportable(importModule, importResolution, objects)
}
