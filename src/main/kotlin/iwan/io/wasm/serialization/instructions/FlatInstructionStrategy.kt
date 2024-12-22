package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.wasm.models.instructions.Instruction
import kotlin.reflect.KClass

internal object FlatInstructionStrategy : InstructionDeserializationStrategy {
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<out Instruction>,
        instance: Instruction?
    ) = checkNotNull(instance)
}
