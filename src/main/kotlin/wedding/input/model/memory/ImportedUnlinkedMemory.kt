package dev.fir3.wedding.input.model.memory

import dev.fir3.iwan.io.wasm.models.MemoryType

internal data class ImportedUnlinkedMemory(
    override val exportName: String?,
    override val index: UInt,
    val memoryName: String,
    override val module: String,
    val sourceModule: String,
    override val type: MemoryType
) : UnlinkedMemory {
    override val debugIdentifier: String get() {
        val b = StringBuilder()
        b.append("I:")
        b.append(module)
        b.append(":")
        b.append(sourceModule)
        b.append(".")
        b.append(memoryName)
        b.append(": ")
        b.append(type)
        return b.toString()
    }
}
