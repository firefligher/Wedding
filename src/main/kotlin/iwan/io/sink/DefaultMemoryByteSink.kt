package dev.fir3.iwan.io.sink

import java.io.ByteArrayOutputStream

internal class DefaultMemoryByteSink : MemoryByteSink {
    private val stream = ByteArrayOutputStream()

    override val buffer: ByteArray
        get() = stream.toByteArray()

    override fun write(src: ByteArray, offset: Int, count: Int) {
        stream.write(src, offset, count)
    }
}
