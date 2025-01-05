package dev.fir3.wedding.io.foundation

import java.io.ByteArrayOutputStream

inline fun withMemoryByteSink(fn: (ByteSink) -> Unit) =
    ByteArrayOutputStream().use { stream ->
        OutputStreamByteSink(stream).use(fn)
        stream.toByteArray()
    }
