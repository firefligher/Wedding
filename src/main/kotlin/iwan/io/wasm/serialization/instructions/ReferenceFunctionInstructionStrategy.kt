package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.instructions.Instruction
import dev.fir3.iwan.io.wasm.models.instructions.ReferenceFunctionInstruction
import kotlin.reflect.KClass

internal object ReferenceFunctionInstructionStrategy :
    InstructionDeserializationStrategy {
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<out Instruction>,
        instance: Instruction?
    ) = ReferenceFunctionInstruction(source.readVarUInt32())
}