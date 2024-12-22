package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.writeVarUInt32
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.instructions.CallInstruction
import java.io.IOException
import kotlin.reflect.KClass

internal object CallInstructionStrategy :
    InstructionSerializationStrategy<CallInstruction> {

    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<CallInstruction>,
        instance: CallInstruction?
    ) = CallInstruction(source.readVarUInt32())

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        instance: CallInstruction
    ) = sink.writeVarUInt32(instance.functionIndex)
}
