package dev.fir3.wedding.linker.merging

import dev.fir3.wedding.linker.pool.*
import dev.fir3.wedding.wasm.FunctionType

fun Pool.mergeFunctionTypes() {
    val indices = mutableMapOf<FunctionType, Pair<String, UInt>>()

    for (functionType in functionTypes) {
        val type = functionType[FunctionTypeInfo::class]!!.type
        val index = indices[type]

        if (index != null) {
            val (module, typeIndex) = index

            functionType[FunctionTypeDuplicate::class] = FunctionTypeDuplicate(
                module = module,
                typeIndex = typeIndex
            )

            continue
        }

        val sourceModule = functionType[SourceModule::class]!!.name
        val sourceIndex = functionType[SourceIndex::class]!!.index

        indices[type] = Pair(sourceModule, sourceIndex)
    }
}
