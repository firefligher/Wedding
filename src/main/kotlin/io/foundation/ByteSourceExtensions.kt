package dev.fir3.wedding.io.foundation

@Throws(IOException::class)
internal fun ByteSource.read(dst: ByteArray) = read(dst, 0u, dst.size.toUInt())

@Throws(IOException::class)
internal fun ByteSource.read(count: UInt): ByteArray {
    val result = ByteArray(count.toInt())
    read(result)
    return result
}

@Throws(IOException::class)
internal fun ByteSource.readInt8() = read(1u)[0]

@Throws(IOException::class)
internal fun ByteSource.readUInt8() = readInt8().toUByte()
