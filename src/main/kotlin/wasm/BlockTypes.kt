package dev.fir3.wedding.wasm

sealed interface BlockType

data object EmptyBlockType : BlockType
data class FunctionBlockType(val value: UInt): BlockType
data class InlineBlockType(val valueType: ValueType): BlockType
