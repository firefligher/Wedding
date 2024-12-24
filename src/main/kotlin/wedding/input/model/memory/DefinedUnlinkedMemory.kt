package dev.fir3.wedding.input.model.memory

import dev.fir3.iwan.io.wasm.models.MemoryType

internal data class DefinedUnlinkedMemory(
    override val exportName: String?,
    override val index: UInt,
    override val module: String,
    override val type: MemoryType
) : UnlinkedMemory
