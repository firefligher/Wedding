package dev.fir3.wedding.input.model.memory

import dev.fir3.iwan.io.wasm.models.MemoryType

internal data class DefinedUnlinkedMemory(
    override val exportName: String?,
    override val index: UInt,
    override val module: String,
    override val type: MemoryType
) : UnlinkedMemory {
    override val debugIdentifier: String get() {
        val b = StringBuilder()
        b.append("D:")
        b.append(module)
        b.append(".")

        if (exportName != null) b.append(exportName)
        else b.append(index)

        b.append(": ")
        b.append(type)
        return b.toString()
    }
}
