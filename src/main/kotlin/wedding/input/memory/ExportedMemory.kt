package dev.fir3.wedding.input.memory

import dev.fir3.iwan.io.wasm.models.MemoryType

internal data class ExportedMemory(
    override val index: UInt,
    val memoryName: String,
    override val moduleName: String,
    override val type: MemoryType
) : MemoryEntry
