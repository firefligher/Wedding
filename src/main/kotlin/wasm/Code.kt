package dev.fir3.wedding.wasm

data class Code(
    val body: List<Instruction>,
    val locals: List<Pair<ValueType, UInt>>
)
