package dev.fir3.iwan.io.common

internal fun ByteArray.toHexString() = joinToString(prefix = "0x") { byte ->
    val hexByte = byte.toString(16)

    if (hexByte.length == 1) "0$hexByte"
    else hexByte
}
