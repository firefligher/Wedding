package dev.fir3.wedding.input.memory

import dev.fir3.iwan.io.wasm.models.MemoryType

internal data class InternalMemory(
    override val index: UInt,
    override val moduleName: String,
    override val type: MemoryType
) : MemoryEntry
