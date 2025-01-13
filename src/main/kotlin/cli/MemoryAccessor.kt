package dev.fir3.wedding.cli

data class MemoryAccessor(
    val getterName: String,
    val setterName: String,
    val memoryAddress: UInt,
    val type: StorableType
)
