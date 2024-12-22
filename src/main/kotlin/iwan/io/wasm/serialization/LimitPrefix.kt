package dev.fir3.iwan.io.wasm.serialization

internal object LimitPrefix {
    const val HAS_NO_MAXIMUM: Byte = 0x00
    const val HAS_MAXIMUM: Byte = 0x01
}
