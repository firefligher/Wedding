package dev.fir3.iwan.io.serialization

import dev.fir3.iwan.io.source.ByteSource
import java.io.IOException
import kotlin.reflect.KClass

internal interface DeserializationContext {
    @Throws(IOException::class)
    fun <TValue : Any> deserialize(
        source: ByteSource,
        valueClass: KClass<TValue>
    ): TValue
}
