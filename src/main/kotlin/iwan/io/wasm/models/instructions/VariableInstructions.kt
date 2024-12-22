package dev.fir3.iwan.io.wasm.models.instructions

import dev.fir3.iwan.io.wasm.serialization.instructions.InstructionInfo
import dev.fir3.iwan.io.wasm.serialization.instructions.VariableInstructionStrategy

sealed interface VariableInstruction : Instruction

@InstructionInfo(0x20u, VariableInstructionStrategy::class)
data class LocalGetInstruction(val localIndex: UInt) : VariableInstruction

@InstructionInfo(0x21u, VariableInstructionStrategy::class)
data class LocalSetInstruction(val localIndex: UInt) : VariableInstruction

@InstructionInfo(0x22u, VariableInstructionStrategy::class)
data class LocalTeeInstruction(val localIndex: UInt) : VariableInstruction

@InstructionInfo(0x23u, VariableInstructionStrategy::class)
data class GlobalGetInstruction(val globalIndex: UInt) : VariableInstruction

@InstructionInfo(0x24u, VariableInstructionStrategy::class)
data class GlobalSetInstruction(val globalIndex: UInt) : VariableInstruction
