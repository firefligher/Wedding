package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.IOException
import dev.fir3.wedding.io.foundation.write

@Throws(IOException::class)
internal fun ByteSink.writeFloat32(value: Float) {
    TODO()
}

@Throws(IOException::class)
internal fun ByteSink.writeFloat64(value: Double) {
    TODO()
}

@Throws(IOException::class)
internal fun ByteSink.writeInt8Vector(value: ByteArray) {
    writeVarUInt32(value.size.toUInt())
    write(*value)
}

@Throws(IOException::class)
internal fun ByteSink.writeName(value: String) {
    val bytes = value.encodeToByteArray()
    writeVarUInt32(bytes.size.toUInt())
    write(*bytes)
}

@Throws(IOException::class)
internal fun ByteSink.writeVarInt32(value: Int) {
    var `continue`: Boolean
    var remainder = value

    do {
        var byte = remainder and 0x7F
        remainder = remainder shr 7

        `continue` = (remainder != -1 || ((byte and 0x40) == 0)) &&
                (remainder != 0 || ((byte and 0x40) == 0x40))

        if (`continue`) byte = byte or 0x80
        write(byte.toByte())
    } while (`continue`)
}

@Throws(IOException::class)
internal fun ByteSink.writeVarInt64(value: Long) {
    var `continue`: Boolean
    var remainder = value

    do {
        var byte = remainder and 0x7F
        remainder = remainder shr 7
        `continue` = (remainder != -1L || ((byte and 0x40) == 0L)) &&
                (remainder != 0L)

        if (`continue`) byte = byte or 0x80
        write(byte.toByte())
    } while (`continue`)
}

fun ByteSink.writeVarUInt32(value: UInt) {
    var `continue`: Boolean
    var remainder = value

    do {
        var byte = remainder and 0x7Fu
        remainder = remainder shr 7
        `continue` = remainder > 0u

        if (`continue`) byte = byte or 0x80u
        write(byte.toByte())
    } while (`continue`)
}

fun ByteSink.writeVarUInt32Vector(vector: Collection<UInt>) {
    writeVarUInt32(vector.size.toUInt())
    vector.forEach(::writeVarUInt32)
}
