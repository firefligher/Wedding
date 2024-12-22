package dev.fir3.iwan.io.wasm.models

sealed interface Data {
    val initializers: List<Byte>
}

data class ActiveData(
    override val initializers: List<Byte>,
    val memoryIndex: UInt,
    val offset: Expression
): Data

data class PassiveData(override val initializers: List<Byte>): Data
