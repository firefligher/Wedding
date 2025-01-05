package dev.fir3.wedding.io.foundation

interface ByteSource {
    @Throws(IOException::class)
    fun read(buffer: ByteArray, offset: UInt, count: UInt)
}
