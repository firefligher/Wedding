package dev.fir3.iwan.io.wasm.models

import dev.fir3.iwan.io.wasm.models.valueTypes.ValueType

data class Function(
    val typeIndex: UInt,
    val locals: List<ValueType>,
    val body: Expression
)
