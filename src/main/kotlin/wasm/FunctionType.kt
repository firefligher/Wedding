package dev.fir3.wedding.wasm

data class FunctionType(
    val parameterTypes: List<ValueType>,
    val resultTypes: List<ValueType>
)
