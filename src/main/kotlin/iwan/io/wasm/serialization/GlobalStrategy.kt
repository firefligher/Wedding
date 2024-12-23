package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.*
import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.serialization.SerializationStrategy
import dev.fir3.iwan.io.serialization.deserialize
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.Global
import dev.fir3.iwan.io.wasm.models.GlobalType
import dev.fir3.iwan.io.wasm.models.instructions.Instruction
import java.io.IOException

internal object GlobalStrategy :
    DeserializationStrategy<Global>,
    SerializationStrategy<Global> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): Global {
        val type = context.deserialize<GlobalType>(source)
        val expression = context.deserialize<Expression>(source)

        return Global(type, expression)
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: Global
    ) {
        context.serialize(sink, value.type)
        context.serialize(sink, value.initializer)
    }
}
