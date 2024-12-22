package dev.fir3.iwan.io.sink

import java.io.IOException

internal interface ByteSink {
    @Throws(IOException::class)
    fun write(src: ByteArray, offset: Int, count: Int)
}
