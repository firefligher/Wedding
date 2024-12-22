package dev.fir3.iwan.io.serialization

import kotlin.reflect.KClass

internal class DeserializationContextBuilder {
    private val _strategies =
        mutableMapOf<KClass<*>, DeserializationStrategy<*>>()

    fun build(): DeserializationContext =
        DefaultDeserializationContext(_strategies)

    @Throws(IllegalStateException::class)
    fun <TModel : Any> register(
        strategy: DeserializationStrategy<TModel>,
        modelClass: KClass<TModel>
    ) {
        if (_strategies[modelClass] != null) {
            throw IllegalStateException("Model has been registered already")
        }

        _strategies[modelClass] = strategy
    }
}
