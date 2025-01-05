package dev.fir3.wedding.io.serialization

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource

interface Strategy<TModel> {
    @Throws(SerializationException::class)
    fun deserialize(source: ByteSource, context: Context): TModel

    @Throws(SerializationException::class)
    fun serialize(instance: TModel, sink: ByteSink, context: Context)
}
