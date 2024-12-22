package dev.fir3.iwan.io.wasm.models.instructions

import dev.fir3.iwan.io.wasm.serialization.instructions.CallInstructionStrategy
import dev.fir3.iwan.io.wasm.serialization.instructions.InstructionInfo

@InstructionInfo(0x10u, CallInstructionStrategy::class)
data class CallInstruction(val functionIndex: UInt): Instruction
