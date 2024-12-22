package dev.fir3.iwan.io.wasm.models

import dev.fir3.iwan.io.wasm.models.valueTypes.ValueType

data class GlobalType(
    val isMutable: Boolean,
    val valueType: ValueType
)
