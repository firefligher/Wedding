package dev.fir3.iwan.io.wasm.models

data class FunctionType(
    val parameterTypes: ResultType,
    val resultTypes: ResultType
)
