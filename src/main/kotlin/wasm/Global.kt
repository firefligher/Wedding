package dev.fir3.wedding.wasm

data class Global(
    val initializer: List<Instruction>,
    val isMutable: Boolean,
    val type: ValueType
)
