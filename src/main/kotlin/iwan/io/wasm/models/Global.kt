package dev.fir3.iwan.io.wasm.models

data class Global(
    val type: GlobalType,
    val initializer: Expression
)
