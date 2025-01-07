package dev.fir3.wedding.io.serialization

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.full.isSuperclassOf

abstract class AbstractContext : Context {
    private val strategies = mutableMapOf<KClass<*>, StrategyContainer<*>>()

    @Throws(SerializationException::class)
    override fun <TModel : Any> deserialize(
        source: ByteSource,
        model: KClass<TModel>
    ): TModel {
        val strategy = strategies[model]
            ?: throw SerializationException(
                "There is no codec to deserialize $model"
            )

        return strategy.deserialize(source, this, model)
    }

    protected fun <TModel : Any> register(
        strategy: Strategy<TModel>,
        model: KClass<TModel>
    ) {
        strategies[model] = StrategyContainer(model, strategy)
    }

    protected inline fun <reified TModel : Any> register(
        strategy: Strategy<TModel>
    ) = register(strategy, TModel::class)

    @Throws(SerializationException::class)
    override fun serialize(sink: ByteSink, instance: Any) {
        val model = instance::class
        val strategy = strategies[model]
            ?: strategies.entries.singleOrNull { (strategyModel, _) ->
                strategyModel.isSuperclassOf(model)
            }?.value
            ?: throw SerializationException(
                "There is no codec to serialize $model"
            )

        strategy.serialize(instance, sink, this)
    }
}

private class StrategyContainer<TModel : Any>(
    private val model: KClass<TModel>,
    private val strategy: Strategy<TModel>
) {
    @Throws(SerializationException::class)
    fun <TResult : Any> deserialize(
        source: ByteSource,
        context: Context,
        resultModel: KClass<TResult>
    ) = resultModel.cast(strategy.deserialize(source, context))

    @Throws(SerializationException::class)
    fun serialize(instance: Any, sink: ByteSink, context: Context) =
        strategy.serialize(model.cast(instance), sink, context)
}
