package dev.fir3.wedding.input.model.memory

import dev.fir3.iwan.io.wasm.models.MemoryType

internal data class ImportedUnlinkedMemory(
    override val exportName: String?,
    override val index: UInt,
    val memoryName: String,
    override val module: String,
    val sourceModule: String,
    override val type: MemoryType
) : UnlinkedMemory
