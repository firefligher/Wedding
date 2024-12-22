package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.sink.*
import dev.fir3.iwan.io.sink.writeFloat32
import dev.fir3.iwan.io.sink.writeFloat64
import dev.fir3.iwan.io.source.*
import dev.fir3.iwan.io.wasm.models.instructions.*
import java.io.IOException
import kotlin.reflect.KClass

internal object ConstInstructionStrategy :
    InstructionSerializationStrategy<ConstInstruction<*>> {

    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<ConstInstruction<*>>,
        instance: ConstInstruction<*>?
    ) = when (model) {
        Float32ConstInstruction::class ->
            Float32ConstInstruction(source.readFloat32())

        Float64ConstInstruction::class ->
            Float64ConstInstruction(source.readFloat64())

        Int32ConstInstruction::class ->
            Int32ConstInstruction(source.readVarInt32())

        Int64ConstInstruction::class ->
            Int64ConstInstruction(source.readVarInt64())

        else -> throw IOException(
            "Unsupported numeric constant type: $model"
        )
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        instance: ConstInstruction<*>
    ) = when (instance) {
        is Float32ConstInstruction -> sink.writeFloat32(instance.constant)
        is Float64ConstInstruction -> sink.writeFloat64(instance.constant)
        is Int32ConstInstruction -> sink.writeVarInt32(instance.constant)
        is Int64ConstInstruction -> sink.writeVarInt64(instance.constant)
    }
}