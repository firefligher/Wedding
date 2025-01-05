package dev.fir3.wedding.io.foundation

@Throws(IOException::class)
fun ByteSink.write(vararg bytes: Byte) = write(bytes, 0u, bytes.size.toUInt())

@OptIn(ExperimentalUnsignedTypes::class)
@Throws(IOException::class)
fun ByteSink.write(vararg bytes: UByte) =
    write(bytes.toByteArray(), 0u, bytes.size.toUInt())
