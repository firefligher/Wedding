package dev.fir3.wedding.cli

import dev.fir3.wedding.wasm.NumberType
import dev.fir3.wedding.wasm.ValueType

enum class StorableType(val wasmType: ValueType) {
    FLOAT32(NumberType.FLOAT32),
    FLOAT64(NumberType.FLOAT64),
    INT8S(NumberType.INT32),
    INT8U(NumberType.INT32),
    INT16S(NumberType.INT32),
    INT16U(NumberType.INT32),
    INT32(NumberType.INT32),
    INT64(NumberType.INT64)
}
