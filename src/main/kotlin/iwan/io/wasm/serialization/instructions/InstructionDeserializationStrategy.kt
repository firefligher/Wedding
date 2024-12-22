package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.wasm.models.instructions.Instruction
import java.io.IOException
import kotlin.reflect.KClass

internal interface InstructionDeserializationStrategy {
    @Throws(IOException::class)
    fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<out Instruction>,
        instance: Instruction?
    ): Instruction
}
