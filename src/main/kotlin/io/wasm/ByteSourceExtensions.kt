package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.foundation.IOException
import dev.fir3.wedding.io.foundation.read
import dev.fir3.wedding.io.foundation.readUInt8

@Throws(IOException::class)
fun ByteSource.expect(vararg expectation: Byte) {
    val actual = read(expectation.size.toUInt())

    if (!actual.contentEquals(expectation)) {
        throw IOException(
            "Expectation failed. Expected: " +
                    "${expectation.contentToString()}, " +
                    "Actual: ${actual.contentToString()}"
        )
    }
}

fun ByteSource.readFloat32(): Float {
    val bytes = read(4u)
    val intValue = bytes[0].toInt() or
            (bytes[1].toInt() shl 8) or
            (bytes[2].toInt() shl 16) or
            (bytes[3].toInt() shl 24)

    return Float.fromBits(intValue)
}

fun ByteSource.readFloat64(): Double {
    val bytes = read(8u)
    val longValue = bytes[0].toLong() or
            (bytes[1].toLong() shl 8) or
            (bytes[2].toLong() shl 16) or
            (bytes[3].toLong() shl 24) or
            (bytes[4].toLong() shl 32) or
            (bytes[5].toLong() shl 40) or
            (bytes[6].toLong() shl 48) or
            (bytes[7].toLong() shl 56)

    return Double.fromBits(longValue)
}

@Throws(IOException::class)
fun ByteSource.readInt8Vector(): ByteArray {
    return read(readVarUInt32())
}

@Throws(IOException::class)
fun ByteSource.readName(): String {
    val nameSize = readVarUInt32()

    if (nameSize > Int.MAX_VALUE.toUInt()) {
        throw IOException("Unsupported length of name")
    }

    return String(read(nameSize))
}

@Throws(IOException::class)
fun ByteSource.readVarInt32(): Int {
    var result = 0u
    var byte: UInt
    var round = 0

    do {
        byte = readUInt8().toUInt()

        if (round == 5 && (byte and 0xF0u) != 0u) {
            throw IOException("Encoded integer exceeds 32 bit")
        }

        result = result or ((byte and 0x7Fu) shl (7 * round))
        round++
    } while (byte and 0x80u != 0u)

    if (round < 5 && (byte and 0x40u) != 0u) {
        result = result or (0xFFFFFFFFu shl (round * 7))
    }

    return result.toInt()
}

@Throws(IOException::class)
fun ByteSource.readVarInt64(): Long {
    var result = 0uL
    var byte: ULong
    var round = 0

    do {
        byte = readUInt8().toULong()

        if (round == 10 && (byte and 0xFCuL) != 0uL) {
            throw IOException("Encoded integer exceeds 32 bit")
        }

        result = result or ((byte and 0x7FuL) shl (7 * round))
        round++
    } while (byte and 0x80uL != 0uL)

    if (round < 10 && (byte and 0x40uL) != 0uL) {
        result = result or (0xFFFFFFFF_FFFFFFFFuL shl (round * 7))
    }

    return result.toLong()
}

@Throws(IOException::class)
fun ByteSource.readVarUInt32(): UInt {
    var result = 0u
    var byte: UInt
    var round = 0

    do {
        byte = readUInt8().toUInt()

        if (round == 5 && (byte and 0xF0u) != 0u) {
            throw IOException("Encoded integer exceeds 32 bit")
        }

        result = result or ((byte and 0x7Fu) shl (7 * round))
        round++
    } while (byte and 0x80u != 0u)

    return result
}

fun ByteSource.readVarUInt32Vector(): List<UInt> {
    val count = readVarUInt32()
    val vector = mutableListOf<UInt>()

    repeat(count.toInt()) { index ->
        vector += readVarUInt32()
    }

    return vector
}
