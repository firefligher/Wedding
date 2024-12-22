package dev.fir3.wedding.input.memory

import dev.fir3.iwan.io.wasm.models.MemoryType

internal data class ImportedMemory(
    override val index: UInt,
    val memoryName: String,
    override val moduleName: String,
    val sourceModule: String,
    override val type: MemoryType
) : MemoryEntry
