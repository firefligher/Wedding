package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.writeVarUInt32
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.instructions.Instruction
import dev.fir3.iwan.io.wasm.models.instructions.ReferenceFunctionInstruction
import kotlin.reflect.KClass

internal object ReferenceFunctionInstructionStrategy :
    InstructionSerializationStrategy<ReferenceFunctionInstruction> {

    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<ReferenceFunctionInstruction>,
        instance: ReferenceFunctionInstruction?
    ) = ReferenceFunctionInstruction(source.readVarUInt32())

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        instance: ReferenceFunctionInstruction
    ) = sink.writeVarUInt32(instance.functionIndex)
}