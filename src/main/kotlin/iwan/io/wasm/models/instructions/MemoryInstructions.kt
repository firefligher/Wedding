package dev.fir3.iwan.io.wasm.models.instructions

import dev.fir3.iwan.io.wasm.serialization.instructions.InstructionInfo
import dev.fir3.iwan.io.wasm.serialization.instructions.MemoryInstructionStrategy

sealed interface MemoryInstruction : Instruction
sealed interface AlignedMemoryInstruction : MemoryInstruction {
    val align: UInt
    val offset: UInt
}

data class DataDropInstruction(val dataIndex: UInt): MemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x2Au, MemoryInstructionStrategy::class)
data class Float32LoadInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x38u, MemoryInstructionStrategy::class)
data class Float32StoreInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x2Bu, MemoryInstructionStrategy::class)
data class Float64LoadInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x39u, MemoryInstructionStrategy::class)
data class Float64StoreInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x28u, MemoryInstructionStrategy::class)
data class Int32LoadInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x2Cu, MemoryInstructionStrategy::class)
data class Int32Load8SInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x2Du, MemoryInstructionStrategy::class)
data class Int32Load8UInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x2Eu, MemoryInstructionStrategy::class)
data class Int32Load16SInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x2Fu, MemoryInstructionStrategy::class)
data class Int32Load16UInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x36u, MemoryInstructionStrategy::class)
data class Int32StoreInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x3Au, MemoryInstructionStrategy::class)
data class Int32Store8Instruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x3Bu, MemoryInstructionStrategy::class)
data class Int32Store16Instruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x29u, MemoryInstructionStrategy::class)
data class Int64LoadInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x30u, MemoryInstructionStrategy::class)
data class Int64Load8SInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x31u, MemoryInstructionStrategy::class)
data class Int64Load8UInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x32u, MemoryInstructionStrategy::class)
data class Int64Load16SInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x33u, MemoryInstructionStrategy::class)
data class Int64Load16UInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x34u, MemoryInstructionStrategy::class)
data class Int64Load32SInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x35u, MemoryInstructionStrategy::class)
data class Int64Load32UInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x37u, MemoryInstructionStrategy::class)
data class Int64StoreInstruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x3Cu, MemoryInstructionStrategy::class)
data class Int64Store8Instruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x3Du, MemoryInstructionStrategy::class)
data class Int64Store16Instruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x3Eu, MemoryInstructionStrategy::class)
data class Int64Store32Instruction(
    override val align: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x40u, MemoryInstructionStrategy::class)
data object MemoryGrowInstruction : MemoryInstruction

data class MemoryInitInstruction(
    val dataIndex: UInt
) : MemoryInstruction

@InstructionInfo<MemoryInstructionStrategy>(0x3Fu, MemoryInstructionStrategy::class)
data object MemorySizeInstruction : MemoryInstruction
