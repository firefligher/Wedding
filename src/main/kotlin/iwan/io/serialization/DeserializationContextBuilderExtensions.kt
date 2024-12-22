package dev.fir3.iwan.io.serialization

internal inline fun <
        reified TModel : Any
> DeserializationContextBuilder.register(
    strategy: DeserializationStrategy<TModel>
) = register(strategy, TModel::class)
