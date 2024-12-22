package dev.fir3.iwan.io.serialization

import dev.fir3.iwan.io.sink.ByteSink
import java.io.IOException

internal interface SerializationStrategy<TValue : Any> {
    @Throws(IOException::class)
    fun serialize(sink: ByteSink, context: SerializationContext, value: TValue)
}
