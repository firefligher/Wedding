package dev.fir3.iwan.io.source

import dev.fir3.iwan.io.common.toHexString
import java.io.IOException

@Throws(IOException::class)
internal fun ByteSource.expect(vararg expectation: Byte) {
    val actual = read(expectation.size)

    if (!actual.contentEquals(expectation)) {
        val hexExpectation = expectation.toHexString()
        val hexActual = actual.toHexString()

        throw IOException(
            "Expectation failed. Expected: $hexExpectation, Actual: $hexActual"
        )
    }
}

@Throws(IOException::class)
internal fun ByteSource.expectInt8(expectation: Byte) = expect(expectation)

@Throws(IOException::class)
internal fun ByteSource.expectUInt8(expectation: UByte) =
    expectInt8(expectation.toByte())

@Throws(IOException::class)
internal fun ByteSource.peek(dst: ByteArray, offset: Int, count: Int) {
    mark()
    read(dst, offset, count)
    reset()
}

@Throws(IOException::class)
internal fun ByteSource.peek(dst: ByteArray) = peek(dst, 0, dst.size)

@Throws(IOException::class)
internal fun ByteSource.peek(count: Int): ByteArray {
    val result = ByteArray(count)
    peek(result)
    return result
}

@Throws(IOException::class)
internal fun ByteSource.peekInt8() = peek(1)[0]

@Throws(IOException::class)
internal fun ByteSource.peekUInt8() = peekInt8().toUByte()

@Throws(IOException::class)
internal fun ByteSource.read(dst: ByteArray) = read(dst, 0, dst.size)

@Throws(IOException::class)
internal fun ByteSource.read(count: Int): ByteArray {
    val result = ByteArray(count)
    read(result)
    return result
}

internal fun ByteSource.readFloat32(): Float {
    val bytes = read(4)
    val intValue = bytes[0].toInt() or
            (bytes[1].toInt() shl 8) or
            (bytes[2].toInt() shl 16) or
            (bytes[3].toInt() shl 24)

    return Float.fromBits(intValue)
}

internal fun ByteSource.readFloat64(): Double {
    val bytes = read(8)
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
internal fun ByteSource.readInt8() = read(1)[0]

internal fun ByteSource.readName(): String {
    val nameSize = readVarUInt32()

    if (nameSize > Int.MAX_VALUE.toUInt()) {
        throw IOException("Unsupported length of name")
    }

    return String(read(nameSize.toInt()))
}

@Throws(IOException::class)
internal fun ByteSource.readUInt8() = readInt8().toUByte()

@Throws(IOException::class)
internal fun ByteSource.readVarInt32(): Int {
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
internal fun ByteSource.readVarInt64(): Long {
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
internal fun ByteSource.readVarUInt32(): UInt {
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
