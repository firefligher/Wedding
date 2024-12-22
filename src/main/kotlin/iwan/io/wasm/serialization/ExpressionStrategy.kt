package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.*
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.write
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.expectUInt8
import dev.fir3.iwan.io.source.peekUInt8
import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.instructions.Instruction
import dev.fir3.iwan.io.wasm.serialization.instructions.InstructionIds

internal object ExpressionStrategy :
    DeserializationStrategy<Expression>,
    SerializationStrategy<Expression> {
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): Expression {
        val body = mutableListOf<Instruction>()

        while (source.peekUInt8() != InstructionIds.END) {
            body.add(context.deserialize(source))
        }

        source.expectUInt8(InstructionIds.END)
        return Expression(body)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: Expression
    ) {
        value.body.forEach { instruction ->
            context.serialize(sink, instruction)
        }

        sink.write(InstructionIds.END)
    }
}
