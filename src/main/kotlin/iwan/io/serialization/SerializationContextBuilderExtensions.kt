package dev.fir3.iwan.io.serialization

internal inline fun <
        reified TValue : Any
> SerializationContextBuilder.register(
    strategy: SerializationStrategy<TValue>
) = register(strategy, TValue::class)
