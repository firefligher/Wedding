package dev.fir3.wedding.input.model.global

import dev.fir3.iwan.io.wasm.models.GlobalType

internal data class ImportedUnlinkedGlobal(
    override val exportName: String?,
    val globalName: String,
    override val index: UInt,
    override val module: String,
    val sourceModule: String,
    override val type: GlobalType
) : UnlinkedGlobal {
    override val debugIdentifier: String get() {
        val b = StringBuilder()
        b.append("I:")
        b.append(module)
        b.append(":")
        b.append(sourceModule)
        b.append(".")
        b.append(globalName)
        b.append(": ")
        b.append(type.valueType)
        return b.toString()
    }
}
