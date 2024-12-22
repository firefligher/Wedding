package dev.fir3.iwan.io.wasm.models.instructions

import dev.fir3.iwan.io.wasm.serialization.instructions.CallIndirectInstructionStrategy
import dev.fir3.iwan.io.wasm.serialization.instructions.InstructionInfo

@InstructionInfo<CallIndirectInstructionStrategy>(
    0x11u,
    CallIndirectInstructionStrategy::class
)
data class CallIndirectInstruction(
    val typeIndex: UInt,
    val tableIndex: UInt
): Instruction
