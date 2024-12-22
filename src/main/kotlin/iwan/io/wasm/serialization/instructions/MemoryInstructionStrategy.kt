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

internal object MemoryInstructionStrategy :
    InstructionSerializationStrategy<MemoryInstruction> {
    private inline fun <TInstruction : MemoryInstruction> build(
        source: ByteSource,
        factory: (align: UInt, offset: UInt) -> TInstruction
    ): TInstruction {
        val align = source.readVarUInt32()
        val offset = source.readVarUInt32()
        return factory(align, offset)
    }

    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<MemoryInstruction>,
        instance: MemoryInstruction?
    ) = when (model) {
        Float32LoadInstruction::class ->
            build(source, ::Float32LoadInstruction)

        Float32StoreInstruction::class ->
            build(source, ::Float32StoreInstruction)

        Float64LoadInstruction::class ->
            build(source, ::Float64LoadInstruction)

        Float64StoreInstruction::class ->
            build(source, ::Float64StoreInstruction)

        Int32LoadInstruction::class -> build(source, ::Int32LoadInstruction)
        Int32Load8SInstruction::class ->
            build(source, ::Int32Load8SInstruction)

        Int32Load8UInstruction::class ->
            build(source, ::Int32Load8UInstruction)

        Int32Load16SInstruction::class ->
            build(source, ::Int32Load16SInstruction)

        Int32Load16UInstruction::class ->
            build(source, ::Int32Load16UInstruction)

        Int32StoreInstruction::class -> build(source, ::Int32StoreInstruction)
        Int32Store8Instruction::class ->
            build(source, ::Int32Store8Instruction)

        Int32Store16Instruction::class ->
            build(source, ::Int32Store16Instruction)

        Int64LoadInstruction::class -> build(source, ::Int64LoadInstruction)
        Int64Load8SInstruction::class ->
            build(source, ::Int64Load8SInstruction)

        Int64Load8UInstruction::class ->
            build(source, ::Int64Load8UInstruction)

        Int64Load16SInstruction::class ->
            build(source, ::Int64Load16SInstruction)

        Int64Load16UInstruction::class ->
            build(source, ::Int64Load16UInstruction)

        Int64Load32SInstruction::class ->
            build(source, ::Int64Load32SInstruction)

        Int64Load32UInstruction::class ->
            build(source, ::Int64Load32UInstruction)

        Int64StoreInstruction::class -> build(source, ::Int64StoreInstruction)
        Int64Store8Instruction::class ->
            build(source, ::Int64Store8Instruction)

        Int64Store16Instruction::class ->
            build(source, ::Int64Store16Instruction)

        Int64Store32Instruction::class ->
            build(source, ::Int64Store32Instruction)

        MemoryGrowInstruction::class -> MemoryGrowInstruction
        MemorySizeInstruction::class -> MemorySizeInstruction
        else -> throw IOException("Invalid memory instruction type: $model")
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        instance: MemoryInstruction
    ) = when (instance) {
        is AlignedMemoryInstruction -> {
            sink.writeVarUInt32(instance.align)
            sink.writeVarUInt32(instance.offset)
        }

        else -> TODO()
    }
}
