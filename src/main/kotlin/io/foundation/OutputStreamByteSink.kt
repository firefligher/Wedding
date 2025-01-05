package dev.fir3.wedding.io.foundation

import java.io.OutputStream

class OutputStreamByteSink(
    private val outputStream: OutputStream
) : AutoCloseable, ByteSink {
    @Throws(IOException::class)
    override fun close() = try {
        outputStream.close()
    } catch (exception: java.io.IOException) {
        throw IOException(exception)
    }


    @Throws(IOException::class)
    override fun write(buffer: ByteArray, offset: UInt, count: UInt) = try {
        outputStream.write(buffer, offset.toInt(), count.toInt())
    } catch (exception: java.io.IOException) {
        throw IOException(exception)
    }
}
