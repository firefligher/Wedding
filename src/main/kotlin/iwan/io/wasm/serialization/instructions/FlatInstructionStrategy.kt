package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.wasm.models.instructions.FlatInstruction
import kotlin.reflect.KClass

internal object FlatInstructionStrategy :
    InstructionSerializationStrategy<FlatInstruction> {

    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<FlatInstruction>,
        instance: FlatInstruction?
    ) = checkNotNull(instance)

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        instance: FlatInstruction
    ) {
        // NOP.
    }
}
