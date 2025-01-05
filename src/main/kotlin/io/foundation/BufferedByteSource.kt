package dev.fir3.wedding.io.foundation

interface BufferedByteSource : ByteSource {
    @Throws(IOException::class)
    fun checkEof(): Boolean

    fun dropMark()
    fun mark()
    fun resetToMark()
}
