package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.writeVarUInt32
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.instructions.*
import java.io.IOException
import kotlin.reflect.KClass

internal object BranchInstructionStrategy :
    InstructionSerializationStrategy<BranchInstruction> {

    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<BranchInstruction>,
        instance: BranchInstruction?
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

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        instance: BranchInstruction
    ) = when (instance) {
        is ConditionalBranchInstruction ->
            sink.writeVarUInt32(instance.labelIndex)

        is TableBranchInstruction -> {
            sink.writeVarUInt32(instance.labelIndices.size.toUInt())
            instance.labelIndices.forEach(sink::writeVarUInt32)
            sink.writeVarUInt32(instance.tableIndex)
        }

        is UnconditionalBranchInstruction ->
            sink.writeVarUInt32(instance.labelIndex)
    }
}
