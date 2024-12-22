package dev.fir3.iwan.io.wasm.models.instructions

import dev.fir3.iwan.io.wasm.serialization.instructions.InstructionInfo
import dev.fir3.iwan.io.wasm.serialization.instructions.ReferenceFunctionInstructionStrategy

@InstructionInfo<ReferenceFunctionInstructionStrategy>(0xD2u, ReferenceFunctionInstructionStrategy::class)
data class ReferenceFunctionInstruction(
    val functionIndex: UInt
): Instruction
