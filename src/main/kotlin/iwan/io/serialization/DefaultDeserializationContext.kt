package dev.fir3.iwan.io.serialization

import dev.fir3.iwan.io.source.ByteSource
import java.io.IOException
import kotlin.reflect.KClass

internal class DefaultDeserializationContext(
    private val strategies: Map<KClass<*>, DeserializationStrategy<*>>
) : DeserializationContext {
    @Throws(IOException::class)
    override fun <TValue : Any> deserialize(
        source: ByteSource,
        valueClass: KClass<TValue>
    ): TValue {
        val strategy = strategies[valueClass] ?: throw IOException(
            "Missing deserialization strategy for " +
                    "'${valueClass.qualifiedName}'"
        )

        // Without a sophisticated map implementation, there is no way to
        // circumvent an unchecked_cast at all.

        @Suppress("UNCHECKED_CAST")
        return strategy.deserialize(source, this) as TValue
    }
}
