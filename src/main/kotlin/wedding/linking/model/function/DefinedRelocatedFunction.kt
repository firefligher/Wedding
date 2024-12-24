package dev.fir3.wedding.linking.model.function

import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.FunctionType
import dev.fir3.iwan.io.wasm.models.valueTypes.ValueType

internal data class DefinedRelocatedFunction(
    override val exportName: String?,
    val expression: Expression,
    override val isStart: Boolean,
    override val type: FunctionType,
    override val index: UInt,
    val locals: List<Pair<ValueType, UInt>>,
    override val module: String,
    override val originalModule: String
) : RelocatedFunction
