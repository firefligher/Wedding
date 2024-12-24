package dev.fir3.wedding.input.model.function

import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.FunctionType
import dev.fir3.iwan.io.wasm.models.valueTypes.ValueType

internal data class DefinedUnlinkedFunction(
    override val exportName: String?,
    val expression: Expression,
    override val index: UInt,
    override val isStart: Boolean,
    val locals: List<Pair<ValueType, UInt>>,
    override val module: String,
    override val type: FunctionType
) : UnlinkedFunction
