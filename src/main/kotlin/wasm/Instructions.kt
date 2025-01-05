package dev.fir3.wedding.wasm

sealed interface Instruction

// Block Instructions

sealed interface BlockTypeInstruction : Instruction {
    val type: BlockType
}

data class BlockInstruction(
    override val type: BlockType,
    val body: List<Instruction>
) : BlockTypeInstruction

data class ConditionalBlockInstruction(
    override val type: BlockType,
    val ifBody: List<Instruction>,
    val elseBody: List<Instruction>?
) : BlockTypeInstruction

data class LoopInstruction(
    override val type: BlockType,
    val body: List<Instruction>
) : BlockTypeInstruction

// Branch Instructions

data class ConditionalBranchInstruction(
    val labelIndex: UInt
): Instruction

data class TableBranchInstruction(
    val labelIndices: List<UInt>,
    val defaultLabelIndex: UInt
): Instruction

data class UnconditionalBranchInstruction(
    val labelIndex: UInt
): Instruction

// Call Instructions

data class CallIndirectInstruction(
    val typeIndex: UInt,
    val tableIndex: UInt
): Instruction

data class CallInstruction(val functionIndex: UInt): Instruction

// Constant Instructions

sealed interface ConstInstruction<TValue> : Instruction {
    val constant: TValue
}

data class Float32ConstInstruction(
    override val constant: Float
) : ConstInstruction<Float>

data class Float64ConstInstruction(
    override val constant: Double
) : ConstInstruction<Double>

data class Int32ConstInstruction(
    override val constant: Int
) : ConstInstruction<Int>

data class Int64ConstInstruction(
    override val constant: Long
) : ConstInstruction<Long>

// Memory Instructions

sealed interface AlignedMemoryInstruction : Instruction {
    val alignment: UInt
    val offset: UInt
}

data class DataDropInstruction(val dataIndex: UInt): Instruction
data class Float32LoadInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Float32StoreInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Float64LoadInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Float64StoreInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int32LoadInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int32Load8SInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int32Load8UInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int32Load16SInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int32Load16UInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int32StoreInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int32Store8Instruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int32Store16Instruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int64LoadInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int64Load8SInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int64Load8UInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int64Load16SInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int64Load16UInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int64Load32SInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int64Load32UInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int64StoreInstruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int64Store8Instruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int64Store16Instruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data class Int64Store32Instruction(
    override val alignment: UInt,
    override val offset: UInt
) : AlignedMemoryInstruction

data object MemoryGrowInstruction : Instruction
data class MemoryInitInstruction(val dataIndex: UInt) : Instruction
data object MemorySizeInstruction : Instruction

// Non-parameterized Instructions

enum class NonParameterizedInstructions : Instruction {
    DROP,
    FLOAT32_ABS,
    FLOAT32_ADD,
    FLOAT32_CEIL,
    FLOAT32_CONVERT_INT32_S,
    FLOAT32_CONVERT_INT32_U,
    FLOAT32_CONVERT_INT64_S,
    FLOAT32_CONVERT_INT64_U,
    FLOAT32_COPYSIGN,
    FLOAT32_DEMOTE_FLOAT64,
    FLOAT32_DIV,
    FLOAT32_EQ,
    FLOAT32_FLOOR,
    FLOAT32_GE,
    FLOAT32_GT,
    FLOAT32_LE,
    FLOAT32_LT,
    FLOAT32_MAX,
    FLOAT32_MIN,
    FLOAT32_MUL,
    FLOAT32_NE,
    FLOAT32_NEAREST,
    FLOAT32_NEG,
    FLOAT32_REINTERPRET_INT32,
    FLOAT32_SQRT,
    FLOAT32_SUB,
    FLOAT32_TRUNC,
    FLOAT64_ABS,
    FLOAT64_ADD,
    FLOAT64_CEIL,
    FLOAT64_CONVERT_INT32_S,
    FLOAT64_CONVERT_INT32_U,
    FLOAT64_CONVERT_INT64_S,
    FLOAT64_CONVERT_INT64_U,
    FLOAT64_COPYSIGN,
    FLOAT64_DIV,
    FLOAT64_EQ,
    FLOAT64_FLOOR,
    FLOAT64_GE,
    FLOAT64_GT,
    FLOAT64_LE,
    FLOAT64_LT,
    FLOAT64_MAX,
    FLOAT64_MIN,
    FLOAT64_MUL,
    FLOAT64_NE,
    FLOAT64_NEAREST,
    FLOAT64_NEG,
    FLOAT64_PROMOTE_FLOAT32,
    FLOAT64_REINTERPRET_INT64,
    FLOAT64_SQRT,
    FLOAT64_SUB,
    FLOAT64_TRUNC,
    INT32_ADD,
    INT32_AND,
    INT32_CLZ,
    INT32_CTZ,
    INT32_DIV_S,
    INT32_DIV_U,
    INT32_EQ,
    INT32_EQZ,
    INT32_EXTEND8_S,
    INT32_EXTEND16_S,
    INT32_GE_S,
    INT32_GE_U,
    INT32_GT_S,
    INT32_GT_U,
    INT32_LE_S,
    INT32_LE_U,
    INT32_LT_S,
    INT32_LT_U,
    INT32_MUL,
    INT32_NE,
    INT32_OR,
    INT32_POPCNT,
    INT32_REINTERPRET_FLOAT32,
    INT32_REM_S,
    INT32_REM_U,
    INT32_ROTL,
    INT32_ROTR,
    INT32_SHL,
    INT32_SHR_S,
    INT32_SHR_U,
    INT32_SUB,
    INT32_TRUNC_FLOAT32_S,
    INT32_TRUNC_FLOAT32_U,
    INT32_TRUNC_FLOAT64_S,
    INT32_TRUNC_FLOAT64_U,
    INT32_WRAP_INT64,
    INT32_XOR,
    INT64_ADD,
    INT64_AND,
    INT64_CLZ,
    INT64_CTZ,
    INT64_DIV_S,
    INT64_DIV_U,
    INT64_EQ,
    INT64_EQZ,
    INT64_EXTEND8_S,
    INT64_EXTEND16_S,
    INT64_EXTEND32_S,
    INT64_EXTEND_INT32_S,
    INT64_EXTEND_INT32_U,
    INT64_GE_S,
    INT64_GE_U,
    INT64_GT_S,
    INT64_GT_U,
    INT64_LE_S,
    INT64_LE_U,
    INT64_LT_S,
    INT64_LT_U,
    INT64_MUL,
    INT64_NE,
    INT64_OR,
    INT64_POPCNT,
    INT64_REINTERPRET_FLOAT64,
    INT64_REM_S,
    INT64_REM_U,
    INT64_ROTL,
    INT64_ROTR,
    INT64_SHL,
    INT64_SHR_S,
    INT64_SHR_U,
    INT64_SUB,
    INT64_TRUNC_FLOAT32_S,
    INT64_TRUNC_FLOAT32_U,
    INT64_TRUNC_FLOAT64_S,
    INT64_TRUNC_FLOAT64_U,
    INT64_XOR,
    NOP,
    REF_IS_NULL,
    RETURN,
    SELECT,
    UNREACHABLE
}

// Reference Function Instructions

data class ReferenceFunctionInstruction(
    val functionIndex: UInt
): Instruction

// Table Instructions

data class ElementDropInstruction(val elementIndex: UInt): Instruction

data class TableCopyInstruction(
    val tableIndex1: UInt,
    val tableIndex2: UInt
): Instruction


data class TableFillInstruction(val tableIndex: UInt): Instruction
data class TableGetInstruction(val tableIndex: UInt): Instruction
data class TableGrowInstruction(val tableIndex: UInt): Instruction
data class TableInitInstruction(
    val tableIndex: UInt,
    val elementIndex: UInt
): Instruction

data class TableSetInstruction(val tableIndex: UInt): Instruction
data class TableSizeInstruction(val tableIndex: UInt): Instruction

// Variable Instructions

data class LocalGetInstruction(val localIndex: UInt) : Instruction
data class LocalSetInstruction(val localIndex: UInt) : Instruction
data class LocalTeeInstruction(val localIndex: UInt) : Instruction
data class GlobalGetInstruction(val globalIndex: UInt) : Instruction
data class GlobalSetInstruction(val globalIndex: UInt) : Instruction
