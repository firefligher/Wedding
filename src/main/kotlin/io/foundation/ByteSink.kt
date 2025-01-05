package dev.fir3.wedding.io.foundation

interface ByteSink {
    @Throws(IOException::class)
    fun write(buffer: ByteArray, offset: UInt, count: UInt)
}
