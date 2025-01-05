package dev.fir3.wedding.wasm

sealed interface ValueType

enum class NumberType : ValueType {
    FLOAT32,
    FLOAT64,
    INT32,
    INT64
}

enum class ReferenceType : ValueType {
    EXTERNAL,
    FUNCTION
}

enum class VectorType : ValueType {
    V128
}
