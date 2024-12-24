package dev.fir3.wedding.linking.model.memory

import dev.fir3.iwan.io.wasm.models.MemoryType

data class ImportedRelocatedMemory(
    override val exportName: String?,
    override val index: UInt,
    val memoryName: String,
    override val module: String,
    override val originalModule: String,
    val sourceModule: String,
    override val type: MemoryType
) : RelocatedMemory
