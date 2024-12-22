package dev.fir3.iwan.io.wasm.models.instructions

import dev.fir3.iwan.io.wasm.serialization.instructions.FlatInstructionStrategy
import dev.fir3.iwan.io.wasm.serialization.instructions.InstructionInfo

enum class FlatInstruction : Instruction {
    @InstructionInfo<FlatInstructionStrategy>(0x1Au, FlatInstructionStrategy::class)
    DROP,

    @InstructionInfo<FlatInstructionStrategy>(0x8Bu, FlatInstructionStrategy::class)
    FLOAT32_ABS,

    @InstructionInfo<FlatInstructionStrategy>(0x92u, FlatInstructionStrategy::class)
    FLOAT32_ADD,

    @InstructionInfo<FlatInstructionStrategy>(0x8Du, FlatInstructionStrategy::class)
    FLOAT32_CEIL,

    @InstructionInfo<FlatInstructionStrategy>(0xB2u, FlatInstructionStrategy::class)
    FLOAT32_CONVERT_INT32_S,

    @InstructionInfo<FlatInstructionStrategy>(0xB3u, FlatInstructionStrategy::class)
    FLOAT32_CONVERT_INT32_U,

    @InstructionInfo<FlatInstructionStrategy>(0xB4u, FlatInstructionStrategy::class)
    FLOAT32_CONVERT_INT64_S,

    @InstructionInfo<FlatInstructionStrategy>(0xB5u, FlatInstructionStrategy::class)
    FLOAT32_CONVERT_INT64_U,

    @InstructionInfo<FlatInstructionStrategy>(0x98u, FlatInstructionStrategy::class)
    FLOAT32_COPYSIGN,

    @InstructionInfo<FlatInstructionStrategy>(0xB6u, FlatInstructionStrategy::class)
    FLOAT32_DEMOTE_FLOAT64,

    @InstructionInfo<FlatInstructionStrategy>(0x95u, FlatInstructionStrategy::class)
    FLOAT32_DIV,

    @InstructionInfo<FlatInstructionStrategy>(0x5Bu, FlatInstructionStrategy::class)
    FLOAT32_EQ,

    @InstructionInfo<FlatInstructionStrategy>(0x8Eu, FlatInstructionStrategy::class)
    FLOAT32_FLOOR,

    @InstructionInfo<FlatInstructionStrategy>(0x60u, FlatInstructionStrategy::class)
    FLOAT32_GE,

    @InstructionInfo<FlatInstructionStrategy>(0x5Eu, FlatInstructionStrategy::class)
    FLOAT32_GT,

    @InstructionInfo<FlatInstructionStrategy>(0x5Fu, FlatInstructionStrategy::class)
    FLOAT32_LE,

    @InstructionInfo<FlatInstructionStrategy>(0x5Du, FlatInstructionStrategy::class)
    FLOAT32_LT,

    @InstructionInfo<FlatInstructionStrategy>(0x97u, FlatInstructionStrategy::class)
    FLOAT32_MAX,

    @InstructionInfo<FlatInstructionStrategy>(0x96u, FlatInstructionStrategy::class)
    FLOAT32_MIN,

    @InstructionInfo<FlatInstructionStrategy>(0x94u, FlatInstructionStrategy::class)
    FLOAT32_MUL,

    @InstructionInfo<FlatInstructionStrategy>(0x5Cu, FlatInstructionStrategy::class)
    FLOAT32_NE,

    @InstructionInfo<FlatInstructionStrategy>(0x90u, FlatInstructionStrategy::class)
    FLOAT32_NEAREST,

    @InstructionInfo<FlatInstructionStrategy>(0x8Cu, FlatInstructionStrategy::class)
    FLOAT32_NEG,

    @InstructionInfo<FlatInstructionStrategy>(0xBEu, FlatInstructionStrategy::class)
    FLOAT32_REINTERPRET_INT32,

    @InstructionInfo<FlatInstructionStrategy>(0x91u, FlatInstructionStrategy::class)
    FLOAT32_SQRT,

    @InstructionInfo<FlatInstructionStrategy>(0x93u, FlatInstructionStrategy::class)
    FLOAT32_SUB,

    @InstructionInfo<FlatInstructionStrategy>(0x8Fu, FlatInstructionStrategy::class)
    FLOAT32_TRUNC,

    @InstructionInfo<FlatInstructionStrategy>(0x99u, FlatInstructionStrategy::class)
    FLOAT64_ABS,

    @InstructionInfo<FlatInstructionStrategy>(0xA0u, FlatInstructionStrategy::class)
    FLOAT64_ADD,

    @InstructionInfo<FlatInstructionStrategy>(0x9Bu, FlatInstructionStrategy::class)
    FLOAT64_CEIL,

    @InstructionInfo<FlatInstructionStrategy>(0xB7u, FlatInstructionStrategy::class)
    FLOAT64_CONVERT_INT32_S,

    @InstructionInfo<FlatInstructionStrategy>(0xB8u, FlatInstructionStrategy::class)
    FLOAT64_CONVERT_INT32_U,

    @InstructionInfo<FlatInstructionStrategy>(0xB9u, FlatInstructionStrategy::class)
    FLOAT64_CONVERT_INT64_S,

    @InstructionInfo<FlatInstructionStrategy>(0xBAu, FlatInstructionStrategy::class)
    FLOAT64_CONVERT_INT64_U,

    @InstructionInfo<FlatInstructionStrategy>(0xA6u, FlatInstructionStrategy::class)
    FLOAT64_COPYSIGN,

    @InstructionInfo<FlatInstructionStrategy>(0xA3u, FlatInstructionStrategy::class)
    FLOAT64_DIV,

    @InstructionInfo<FlatInstructionStrategy>(0x61u, FlatInstructionStrategy::class)
    FLOAT64_EQ,

    @InstructionInfo<FlatInstructionStrategy>(0x9Cu, FlatInstructionStrategy::class)
    FLOAT64_FLOOR,

    @InstructionInfo<FlatInstructionStrategy>(0x66u, FlatInstructionStrategy::class)
    FLOAT64_GE,

    @InstructionInfo<FlatInstructionStrategy>(0x64u, FlatInstructionStrategy::class)
    FLOAT64_GT,

    @InstructionInfo<FlatInstructionStrategy>(0x65u, FlatInstructionStrategy::class)
    FLOAT64_LE,

    @InstructionInfo<FlatInstructionStrategy>(0x63u, FlatInstructionStrategy::class)
    FLOAT64_LT,

    @InstructionInfo<FlatInstructionStrategy>(0xA5u, FlatInstructionStrategy::class)
    FLOAT64_MAX,

    @InstructionInfo<FlatInstructionStrategy>(0xA4u, FlatInstructionStrategy::class)
    FLOAT64_MIN,

    @InstructionInfo<FlatInstructionStrategy>(0xA2u, FlatInstructionStrategy::class)
    FLOAT64_MUL,

    @InstructionInfo<FlatInstructionStrategy>(0x62u, FlatInstructionStrategy::class)
    FLOAT64_NE,

    @InstructionInfo<FlatInstructionStrategy>(0x9Eu, FlatInstructionStrategy::class)
    FLOAT64_NEAREST,

    @InstructionInfo<FlatInstructionStrategy>(0x9Au, FlatInstructionStrategy::class)
    FLOAT64_NEG,

    @InstructionInfo<FlatInstructionStrategy>(0xBBu, FlatInstructionStrategy::class)
    FLOAT64_PROMOTE_FLOAT32,

    @InstructionInfo<FlatInstructionStrategy>(0xBFu, FlatInstructionStrategy::class)
    FLOAT64_REINTERPRET_INT64,

    @InstructionInfo<FlatInstructionStrategy>(0x9Fu, FlatInstructionStrategy::class)
    FLOAT64_SQRT,

    @InstructionInfo<FlatInstructionStrategy>(0xA1u, FlatInstructionStrategy::class)
    FLOAT64_SUB,

    @InstructionInfo<FlatInstructionStrategy>(0x9Du, FlatInstructionStrategy::class)
    FLOAT64_TRUNC,

    @InstructionInfo<FlatInstructionStrategy>(0x6Au, FlatInstructionStrategy::class)
    INT32_ADD,

    @InstructionInfo<FlatInstructionStrategy>(0x71u, FlatInstructionStrategy::class)
    INT32_AND,

    @InstructionInfo<FlatInstructionStrategy>(0x67u, FlatInstructionStrategy::class)
    INT32_CLZ,

    @InstructionInfo<FlatInstructionStrategy>(0x68u, FlatInstructionStrategy::class)
    INT32_CTZ,

    @InstructionInfo<FlatInstructionStrategy>(0x6Du, FlatInstructionStrategy::class)
    INT32_DIV_S,

    @InstructionInfo<FlatInstructionStrategy>(0x6Eu, FlatInstructionStrategy::class)
    INT32_DIV_U,

    @InstructionInfo<FlatInstructionStrategy>(0x46u, FlatInstructionStrategy::class)
    INT32_EQ,

    @InstructionInfo<FlatInstructionStrategy>(0x45u, FlatInstructionStrategy::class)
    INT32_EQZ,

    @InstructionInfo<FlatInstructionStrategy>(0xC0u, FlatInstructionStrategy::class)
    INT32_EXTEND8_S,

    @InstructionInfo<FlatInstructionStrategy>(0xC1u, FlatInstructionStrategy::class)
    INT32_EXTEND16_S,

    @InstructionInfo<FlatInstructionStrategy>(0x4Eu, FlatInstructionStrategy::class)
    INT32_GE_S,

    @InstructionInfo<FlatInstructionStrategy>(0x4Fu, FlatInstructionStrategy::class)
    INT32_GE_U,

    @InstructionInfo<FlatInstructionStrategy>(0x4Au, FlatInstructionStrategy::class)
    INT32_GT_S,

    @InstructionInfo<FlatInstructionStrategy>(0x4Bu, FlatInstructionStrategy::class)
    INT32_GT_U,

    @InstructionInfo<FlatInstructionStrategy>(0x4Cu, FlatInstructionStrategy::class)
    INT32_LE_S,

    @InstructionInfo<FlatInstructionStrategy>(0x4Du, FlatInstructionStrategy::class)
    INT32_LE_U,

    @InstructionInfo<FlatInstructionStrategy>(0x48u, FlatInstructionStrategy::class)
    INT32_LT_S,

    @InstructionInfo<FlatInstructionStrategy>(0x49u, FlatInstructionStrategy::class)
    INT32_LT_U,

    @InstructionInfo<FlatInstructionStrategy>(0x6Cu, FlatInstructionStrategy::class)
    INT32_MUL,

    @InstructionInfo<FlatInstructionStrategy>(0x47u, FlatInstructionStrategy::class)
    INT32_NE,

    @InstructionInfo<FlatInstructionStrategy>(0x72u, FlatInstructionStrategy::class)
    INT32_OR,

    @InstructionInfo<FlatInstructionStrategy>(0x69u, FlatInstructionStrategy::class)
    INT32_POPCNT,

    @InstructionInfo<FlatInstructionStrategy>(0xBCu, FlatInstructionStrategy::class)
    INT32_REINTERPRET_FLOAT32,

    @InstructionInfo<FlatInstructionStrategy>(0x6Fu, FlatInstructionStrategy::class)
    INT32_REM_S,

    @InstructionInfo<FlatInstructionStrategy>(0x70u, FlatInstructionStrategy::class)
    INT32_REM_U,

    @InstructionInfo<FlatInstructionStrategy>(0x77u, FlatInstructionStrategy::class)
    INT32_ROTL,

    @InstructionInfo<FlatInstructionStrategy>(0x78u, FlatInstructionStrategy::class)
    INT32_ROTR,

    @InstructionInfo<FlatInstructionStrategy>(0x74u, FlatInstructionStrategy::class)
    INT32_SHL,

    @InstructionInfo<FlatInstructionStrategy>(0x75u, FlatInstructionStrategy::class)
    INT32_SHR_S,

    @InstructionInfo<FlatInstructionStrategy>(0x76u, FlatInstructionStrategy::class)
    INT32_SHR_U,

    @InstructionInfo<FlatInstructionStrategy>(0x6Bu, FlatInstructionStrategy::class)
    INT32_SUB,

    @InstructionInfo<FlatInstructionStrategy>(0xA8u, FlatInstructionStrategy::class)
    INT32_TRUNC_FLOAT32_S,

    @InstructionInfo<FlatInstructionStrategy>(0xA9u, FlatInstructionStrategy::class)
    INT32_TRUNC_FLOAT32_U,

    @InstructionInfo<FlatInstructionStrategy>(0xAAu, FlatInstructionStrategy::class)
    INT32_TRUNC_FLOAT64_S,

    @InstructionInfo<FlatInstructionStrategy>(0xABu, FlatInstructionStrategy::class)
    INT32_TRUNC_FLOAT64_U,

    @InstructionInfo<FlatInstructionStrategy>(0xA7u, FlatInstructionStrategy::class)
    INT32_WRAP_INT64,

    @InstructionInfo<FlatInstructionStrategy>(0x73u, FlatInstructionStrategy::class)
    INT32_XOR,

    @InstructionInfo<FlatInstructionStrategy>(0x7Cu, FlatInstructionStrategy::class)
    INT64_ADD,

    @InstructionInfo<FlatInstructionStrategy>(0x83u, FlatInstructionStrategy::class)
    INT64_AND,

    @InstructionInfo<FlatInstructionStrategy>(0x79u, FlatInstructionStrategy::class)
    INT64_CLZ,

    @InstructionInfo<FlatInstructionStrategy>(0x7Au, FlatInstructionStrategy::class)
    INT64_CTZ,

    @InstructionInfo<FlatInstructionStrategy>(0x7Fu, FlatInstructionStrategy::class)
    INT64_DIV_S,

    @InstructionInfo<FlatInstructionStrategy>(0x80u, FlatInstructionStrategy::class)
    INT64_DIV_U,

    @InstructionInfo<FlatInstructionStrategy>(0x51u, FlatInstructionStrategy::class)
    INT64_EQ,

    @InstructionInfo<FlatInstructionStrategy>(0x50u, FlatInstructionStrategy::class)
    INT64_EQZ,

    @InstructionInfo<FlatInstructionStrategy>(0xC2u, FlatInstructionStrategy::class)
    INT64_EXTEND8_S,

    @InstructionInfo<FlatInstructionStrategy>(0xC3u, FlatInstructionStrategy::class)
    INT64_EXTEND16_S,

    @InstructionInfo<FlatInstructionStrategy>(0xC4u, FlatInstructionStrategy::class)
    INT64_EXTEND32_S,

    @InstructionInfo<FlatInstructionStrategy>(0xACu, FlatInstructionStrategy::class)
    INT64_EXTEND_INT32_S,

    @InstructionInfo<FlatInstructionStrategy>(0xADu, FlatInstructionStrategy::class)
    INT64_EXTEND_INT32_U,

    @InstructionInfo<FlatInstructionStrategy>(0x59u, FlatInstructionStrategy::class)
    INT64_GE_S,

    @InstructionInfo<FlatInstructionStrategy>(0x5Au, FlatInstructionStrategy::class)
    INT64_GE_U,

    @InstructionInfo<FlatInstructionStrategy>(0x55u, FlatInstructionStrategy::class)
    INT64_GT_S,

    @InstructionInfo<FlatInstructionStrategy>(0x56u, FlatInstructionStrategy::class)
    INT64_GT_U,

    @InstructionInfo<FlatInstructionStrategy>(0x57u, FlatInstructionStrategy::class)
    INT64_LE_S,

    @InstructionInfo<FlatInstructionStrategy>(0x58u, FlatInstructionStrategy::class)
    INT64_LE_U,

    @InstructionInfo<FlatInstructionStrategy>(0x53u, FlatInstructionStrategy::class)
    INT64_LT_S,

    @InstructionInfo<FlatInstructionStrategy>(0x54u, FlatInstructionStrategy::class)
    INT64_LT_U,

    @InstructionInfo<FlatInstructionStrategy>(0x7Eu, FlatInstructionStrategy::class)
    INT64_MUL,

    @InstructionInfo<FlatInstructionStrategy>(0x52u, FlatInstructionStrategy::class)
    INT64_NE,

    @InstructionInfo<FlatInstructionStrategy>(0x84u, FlatInstructionStrategy::class)
    INT64_OR,

    @InstructionInfo<FlatInstructionStrategy>(0x7Bu, FlatInstructionStrategy::class)
    INT64_POPCNT,

    @InstructionInfo<FlatInstructionStrategy>(0xBDu, FlatInstructionStrategy::class)
    INT64_REINTERPRET_FLOAT64,

    @InstructionInfo<FlatInstructionStrategy>(0x81u, FlatInstructionStrategy::class)
    INT64_REM_S,

    @InstructionInfo<FlatInstructionStrategy>(0x82u, FlatInstructionStrategy::class)
    INT64_REM_U,

    @InstructionInfo<FlatInstructionStrategy>(0x89u, FlatInstructionStrategy::class)
    INT64_ROTL,

    @InstructionInfo<FlatInstructionStrategy>(0x8Au, FlatInstructionStrategy::class)
    INT64_ROTR,

    @InstructionInfo<FlatInstructionStrategy>(0x86u, FlatInstructionStrategy::class)
    INT64_SHL,

    @InstructionInfo<FlatInstructionStrategy>(0x87u, FlatInstructionStrategy::class)
    INT64_SHR_S,

    @InstructionInfo<FlatInstructionStrategy>(0x88u, FlatInstructionStrategy::class)
    INT64_SHR_U,

    @InstructionInfo<FlatInstructionStrategy>(0x7Du, FlatInstructionStrategy::class)
    INT64_SUB,

    @InstructionInfo<FlatInstructionStrategy>(0xAEu, FlatInstructionStrategy::class)
    INT64_TRUNC_FLOAT32_S,

    @InstructionInfo<FlatInstructionStrategy>(0xAFu, FlatInstructionStrategy::class)
    INT64_TRUNC_FLOAT32_U,

    @InstructionInfo<FlatInstructionStrategy>(0xB0u, FlatInstructionStrategy::class)
    INT64_TRUNC_FLOAT64_S,

    @InstructionInfo<FlatInstructionStrategy>(0xB1u, FlatInstructionStrategy::class)
    INT64_TRUNC_FLOAT64_U,

    @InstructionInfo<FlatInstructionStrategy>(0x85u, FlatInstructionStrategy::class)
    INT64_XOR,

    @InstructionInfo<FlatInstructionStrategy>(0x01u, FlatInstructionStrategy::class)
    NOP,

    @InstructionInfo<FlatInstructionStrategy>(0xD1u, FlatInstructionStrategy::class)
    REF_IS_NULL,

    @InstructionInfo<FlatInstructionStrategy>(0x0Fu, FlatInstructionStrategy::class)
    RETURN,

    @InstructionInfo<FlatInstructionStrategy>(0x1Bu, FlatInstructionStrategy::class)
    SELECT,

    @InstructionInfo<FlatInstructionStrategy>(0x00u, FlatInstructionStrategy::class)
    UNREACHABLE
}
