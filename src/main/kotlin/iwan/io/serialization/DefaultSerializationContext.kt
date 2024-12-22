package dev.fir3.iwan.io.serialization

import dev.fir3.iwan.io.sink.ByteSink
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

internal class DefaultSerializationContext(
    strategies: Map<KClass<*>, SerializationStrategy<*>>
) : SerializationContext {
    private val strategies = strategies.mapValues { (_, strategy) ->
        SerializationStrategyWrapper(strategy)
    }

    override fun serialize(sink: ByteSink, value: Any) {
        val valueClass = value.javaClass.kotlin
        //println("Serializing: ${valueClass.simpleName}")
        val strategy = strategies[valueClass] ?: strategies
            .filterKeys(valueClass::isSubclassOf)
            .firstNotNullOf(
                Map.Entry<KClass<*>, SerializationStrategyWrapper<*>>::value
            )

        strategy.serialize(sink, this, value)
    }
}

private class SerializationStrategyWrapper<TValue : Any>(
    private val strategy: SerializationStrategy<TValue>
) {
    @Suppress("UNCHECKED_CAST")
    fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: Any
    ) = strategy.serialize(sink, context, value as TValue)
}
