package dev.fir3.wedding.wasm

sealed interface Data {
    val initializers: List<Byte>
}

data class ActiveData(
    override val initializers: List<Byte>,
    val memoryIndex: UInt,
    val offset: List<Instruction>
): Data

data class PassiveData(override val initializers: List<Byte>): Data
