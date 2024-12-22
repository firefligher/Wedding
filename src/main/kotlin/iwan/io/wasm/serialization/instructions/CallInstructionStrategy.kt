package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.instructions.CallInstruction
import dev.fir3.iwan.io.wasm.models.instructions.Instruction
import java.io.IOException
import kotlin.reflect.KClass

internal object CallInstructionStrategy : InstructionDeserializationStrategy {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<out Instruction>,
        instance: Instruction?
    ) = CallInstruction(source.readVarUInt32())
}
