package dev.fir3.iwan.io.serialization

import dev.fir3.iwan.io.sink.ByteSink
import java.io.IOException

internal interface SerializationContext {
    @Throws(IOException::class)
    fun serialize(sink: ByteSink, value: Any)
}
