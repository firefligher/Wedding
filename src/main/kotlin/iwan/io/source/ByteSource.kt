package dev.fir3.iwan.io.source

import java.io.IOException

interface ByteSource {
    fun isEof(): Boolean

    fun mark()

    @Throws(IOException::class)
    fun read(dst: ByteArray, offset: Int, count: Int)

    fun reset(unmark: Boolean = true)

    fun unmark()
}
