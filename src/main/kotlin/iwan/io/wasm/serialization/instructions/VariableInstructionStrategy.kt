package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readUInt8
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.instructions.*
import java.io.IOException
import kotlin.reflect.KClass

internal object VariableInstructionStrategy :
    InstructionDeserializationStrategy {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<out Instruction>,
        instance: Instruction?
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
}
