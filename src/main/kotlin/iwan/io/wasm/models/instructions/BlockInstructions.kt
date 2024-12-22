package dev.fir3.iwan.io.wasm.models.instructions

import dev.fir3.iwan.io.wasm.models.instructions.blockTypes.BlockType
import dev.fir3.iwan.io.wasm.serialization.instructions.BlockTypeInstructionStrategy
import dev.fir3.iwan.io.wasm.serialization.instructions.InstructionInfo

sealed interface BlockTypeInstruction : Instruction {
    val type: BlockType
}

@InstructionInfo<BlockTypeInstructionStrategy>(
    0x02u,
    BlockTypeInstructionStrategy::class
)
data class BlockInstruction(
    override val type: BlockType,
    val body: List<Instruction>
) : BlockTypeInstruction

@InstructionInfo<BlockTypeInstructionStrategy>(
    0x04u,
    BlockTypeInstructionStrategy::class
)
data class ConditionalBlockInstruction(
    override val type: BlockType,
    val ifBody: List<Instruction>,
    val elseBody: List<Instruction>?
) : BlockTypeInstruction

@InstructionInfo<BlockTypeInstructionStrategy>(
    0x03u,
    BlockTypeInstructionStrategy::class
)
data class LoopInstruction(
    override val type: BlockType,
    val body: List<Instruction>
) : BlockTypeInstruction
