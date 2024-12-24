package dev.fir3.wedding.input.model.function

import dev.fir3.iwan.io.wasm.models.FunctionType

internal data class ImportedUnlinkedFunction(
    override val exportName: String?,
    val functionName: String,
    override val index: UInt,
    override val isStart: Boolean,
    override val module: String,
    val sourceModule: String,
    override val type: FunctionType
) : UnlinkedFunction {
    override val debugIdentifier: String get() {
        val b = StringBuilder()
        b.append("I:")
        b.append(module)
        b.append(":")
        b.append(sourceModule)
        b.append('.')
        b.append(functionName)
        b.append("(")
        b.append(type.parameterTypes.joinToString(","))
        b.append(")")
        b.append(":")
        b.append(type.resultTypes.joinToString(","))

        return b.toString()
    }
}
