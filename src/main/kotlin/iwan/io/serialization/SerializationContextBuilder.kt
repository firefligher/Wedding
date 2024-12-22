package dev.fir3.iwan.io.serialization

import kotlin.reflect.KClass

internal class SerializationContextBuilder {
    private val strategies =
        mutableMapOf<KClass<*>, SerializationStrategy<*>>()

    fun build(): SerializationContext = DefaultSerializationContext(strategies)

    @Throws
    fun <TValue : Any> register(
        strategy: SerializationStrategy<TValue>,
        valueClass: KClass<TValue>
    ) {
        if (strategies[valueClass] != null) {
            throw IllegalStateException("Value has been registered already")
        }

        strategies[valueClass] = strategy
    }
}
