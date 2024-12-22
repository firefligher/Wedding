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

internal object VariableInstructionStrategy :
    InstructionSerializationStrategy<VariableInstruction> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<VariableInstruction>,
        instance: VariableInstruction?
    ) = when (model) {
        GlobalGetInstruction::class ->
            GlobalGetInstruction(source.readVarUInt32())

        GlobalSetInstruction::class ->
            GlobalSetInstruction(source.readVarUInt32())

        LocalGetInstruction::class ->
            LocalGetInstruction(source.readVarUInt32())

        LocalSetInstruction::class ->
            LocalSetInstruction(source.readVarUInt32())

        LocalTeeInstruction::class ->
            LocalTeeInstruction(source.readVarUInt32())

        else -> throw IOException(
            "Invalid variable instruction type: $model"
        )
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        instance: VariableInstruction
    ) = when (instance) {
        is GlobalGetInstruction -> sink.writeVarUInt32(instance.globalIndex)
        is GlobalSetInstruction -> sink.writeVarUInt32(instance.globalIndex)
        is LocalGetInstruction -> sink.writeVarUInt32(instance.localIndex)
        is LocalSetInstruction -> sink.writeVarUInt32(instance.localIndex)
        is LocalTeeInstruction -> sink.writeVarUInt32(instance.localIndex)
    }
}
