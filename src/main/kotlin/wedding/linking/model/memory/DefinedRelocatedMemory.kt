package dev.fir3.wedding.linking.model.memory

import dev.fir3.iwan.io.wasm.models.MemoryType

data class DefinedRelocatedMemory(
    override val exportName: String?,
    override val index: UInt,
    override val module: String,
    override val originalModule: String,
    override val type: MemoryType
) : RelocatedMemory
