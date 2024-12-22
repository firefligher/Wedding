package dev.fir3.iwan.io.wasm.models.instructions

import dev.fir3.iwan.io.wasm.serialization.instructions.BranchInstructionStrategy
import dev.fir3.iwan.io.wasm.serialization.instructions.InstructionInfo

sealed interface BranchInstruction : Instruction

@InstructionInfo(0x0Cu, BranchInstructionStrategy::class)
data class UnconditionalBranchInstruction(
    val labelIndex: UInt
): BranchInstruction

@InstructionInfo(0x0Du, BranchInstructionStrategy::class)
data class ConditionalBranchInstruction(
    val labelIndex: UInt
): BranchInstruction

@InstructionInfo(0x0Eu, BranchInstructionStrategy::class)
data class TableBranchInstruction(
    val labelIndices: List<UInt>,
    val tableIndex: UInt
): BranchInstruction
