package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.wasm.models.instructions.Instruction
import java.io.IOException
import kotlin.reflect.KClass

internal interface InstructionSerializationStrategy<
        TInstruction : Instruction
> {
    @Throws(IOException::class)
    fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<TInstruction>,
        instance: TInstruction?
    ): Instruction

    @Throws(IOException::class)
    fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        instance: TInstruction
    )
}
