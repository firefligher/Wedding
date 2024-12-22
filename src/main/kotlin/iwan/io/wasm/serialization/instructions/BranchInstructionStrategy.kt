package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readUInt8
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.instructions.*
import java.io.IOException
import kotlin.reflect.KClass

internal object BranchInstructionStrategy :
    InstructionDeserializationStrategy {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<out Instruction>,
        instance: Instruction?
    ) = when (model) {
        UnconditionalBranchInstruction::class ->
            UnconditionalBranchInstruction(source.readVarUInt32())

        ConditionalBranchInstruction::class ->
            ConditionalBranchInstruction(source.readVarUInt32())

        TableBranchInstruction::class -> {
            val indicesCount = source.readVarUInt32()
            val indices = mutableListOf<UInt>()

            for (i in 0u until indicesCount) {
                indices.add(source.readVarUInt32())
            }

            val tableIndex = source.readVarUInt32()
            TableBranchInstruction(indices, tableIndex)
        }

        else -> throw IOException("Invalid branch instruction type: $model")
    }
}
