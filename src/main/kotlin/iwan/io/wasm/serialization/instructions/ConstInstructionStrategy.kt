package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.source.*
import dev.fir3.iwan.io.source.readUInt8
import dev.fir3.iwan.io.source.readVarInt32
import dev.fir3.iwan.io.source.readVarInt64
import dev.fir3.iwan.io.wasm.models.instructions.*
import java.io.IOException
import kotlin.reflect.KClass

internal object ConstInstructionStrategy : InstructionDeserializationStrategy {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<out Instruction>,
        instance: Instruction?
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
}