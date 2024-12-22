package dev.fir3.iwan.io.serialization

import dev.fir3.iwan.io.source.ByteSource
import java.io.IOException

internal interface DeserializationStrategy<TValue : Any> {
    @Throws(IOException::class)
    fun deserialize(source: ByteSource, context: DeserializationContext): TValue
}
