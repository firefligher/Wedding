package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.writeVarUInt32
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.instructions.CallIndirectInstruction
import dev.fir3.iwan.io.wasm.models.instructions.Instruction
import java.io.IOException
import kotlin.reflect.KClass

internal object CallIndirectInstructionStrategy :
    InstructionSerializationStrategy<CallIndirectInstruction> {

    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<CallIndirectInstruction>,
        instance: CallIndirectInstruction?
    ): CallIndirectInstruction {
        val typeIndex = source.readVarUInt32()
        val tableIndex = source.readVarUInt32()

        return CallIndirectInstruction(typeIndex, tableIndex)
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        instance: CallIndirectInstruction
    ) {
        sink.writeVarUInt32(instance.typeIndex)
        sink.writeVarUInt32(instance.tableIndex)
    }
}