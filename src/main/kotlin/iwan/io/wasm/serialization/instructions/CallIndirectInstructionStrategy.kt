package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.instructions.CallIndirectInstruction
import dev.fir3.iwan.io.wasm.models.instructions.Instruction
import java.io.IOException
import kotlin.reflect.KClass

internal object CallIndirectInstructionStrategy :
    InstructionDeserializationStrategy {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<out Instruction>,
        instance: Instruction?
    ): CallIndirectInstruction {
        val typeIndex = source.readVarUInt32()
        val tableIndex = source.readVarUInt32()

        return CallIndirectInstruction(typeIndex, tableIndex)
    }
}