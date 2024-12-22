package dev.fir3.iwan.io.wasm.models.instructions

import dev.fir3.iwan.io.wasm.serialization.instructions.ConstInstructionStrategy
import dev.fir3.iwan.io.wasm.serialization.instructions.InstructionInfo

sealed interface ConstInstruction<TValue> : Instruction {
    val constant: TValue
}

@InstructionInfo(0x43u, ConstInstructionStrategy::class)
data class Float32ConstInstruction(
    override val constant: Float
) : ConstInstruction<Float>

@InstructionInfo(0x44u, ConstInstructionStrategy::class)
data class Float64ConstInstruction(
    override val constant: Double
) : ConstInstruction<Double>

@InstructionInfo(0x41u, ConstInstructionStrategy::class)
data class Int32ConstInstruction(
    override val constant: Int
) : ConstInstruction<Int>

@InstructionInfo(0x42u, ConstInstructionStrategy::class)
data class Int64ConstInstruction(
    override val constant: Long
) : ConstInstruction<Long>
