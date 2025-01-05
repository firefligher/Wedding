package dev.fir3.wedding.io.serialization

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import kotlin.reflect.KClass

interface Context {
    @Throws(SerializationException::class)
    fun <TModel : Any> deserialize(
        source: ByteSource,
        model: KClass<TModel>
    ): TModel

    @Throws(SerializationException::class)
    fun serialize(sink: ByteSink, instance: Any)
}
