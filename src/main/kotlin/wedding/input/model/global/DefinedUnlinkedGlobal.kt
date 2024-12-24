package dev.fir3.wedding.input.model.global

import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.GlobalType

internal data class DefinedUnlinkedGlobal(
    override val exportName: String?,
    override val index: UInt,
    val initializer: Expression,
    override val module: String,
    override val type: GlobalType
) : UnlinkedGlobal {
    override val debugIdentifier: String get() {
        val b = StringBuilder()
        b.append("D:")
        b.append(module)
        b.append(".")

        if (exportName != null) b.append(exportName)
        else b.append(index)

        b.append(": ")
        b.append(type.valueType)
        return b.toString()
    }
}
