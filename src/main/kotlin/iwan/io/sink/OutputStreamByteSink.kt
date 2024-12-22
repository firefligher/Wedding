package dev.fir3.iwan.io.sink

import java.io.IOException
import java.io.OutputStream

internal class OutputStreamByteSink(
    private val outputStream: OutputStream
) : ByteSink {
    @Throws(IOException::class)
    override fun write(src: ByteArray, offset: Int, count: Int) {
        outputStream.write(src, offset, count)
    }
}
