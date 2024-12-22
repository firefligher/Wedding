package dev.fir3.iwan.io.wasm.serialization.valueTypes

internal object ValueTypeIds {
    const val EXTERNAL_REFERENCE: Byte = 0x6F
    const val FLOAT32: Byte = 0x7D
    const val FLOAT64: Byte = 0x7C
    const val FUNCTION_REFERENCE: Byte = 0x70
    const val INT32: Byte = 0x7F
    const val INT64: Byte = 0x7E
    const val VECTOR128: Byte = 0x7B
}
