package dev.fir3.wedding.io.foundation

import java.io.InputStream

class InputStreamByteSource(
    private val inputStream: InputStream
) : AutoCloseable, ByteSource {
    @Throws(IOException::class)
    override fun close() = try {
        inputStream.close()
    } catch (exception: java.io.IOException) {
        throw IOException(exception)
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray, offset: UInt, count: UInt) = try {
        var readBytesTotal = 0u

        while (readBytesTotal < count) {
            val readBytes = inputStream.read(
                buffer,
                (offset + readBytesTotal).toInt(),
                (count - readBytesTotal).toInt()
            )

            if (readBytes == -1) throw IOException("Reached end of stream")
            readBytesTotal += readBytes.toUInt()
        }
    } catch (exception: java.io.IOException) {
        throw IOException(exception)
    }
}
