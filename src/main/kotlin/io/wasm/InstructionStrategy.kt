package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.foundation.readUInt8
import dev.fir3.wedding.io.foundation.write
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.SerializationException
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.io.serialization.deserialize
import dev.fir3.wedding.wasm.*
import dev.fir3.wedding.wasm.NonParameterizedInstructions.*

object InstructionStrategy : Strategy<Instruction> {
    override fun deserialize(
        source: ByteSource,
        context: Context
    ) = when (val opcode = source.readUInt8().toUInt()) {
        0x00u -> UNREACHABLE
        0x01u -> NOP
        0x02u -> {
            val type = context.deserialize<BlockType>(source)
            val instructions = context.deserializeVector<Instruction>(source)
            source.expect(0x0B)

            BlockInstruction(
                body = instructions,
                type = type
            )
        }
        0x03u -> {
            val type = context.deserialize<BlockType>(source)
            val instructions = context.deserializeVector<Instruction>(source)
            source.expect(0x0B)

            LoopInstruction(
                body = instructions,
                type = type
            )
        }
        0x04u -> {
            val type = context.deserialize<BlockType>(source)
            val ifBody = context.deserializeVector<Instruction>(source)
            val indication = source.readUInt8().toUInt()
            val elseBody: List<Instruction>?

            when (indication) {
                0x05u -> {
                    elseBody = context.deserializeVector<Instruction>(source)
                    source.expect(0x0B)
                }
                0x0Bu -> elseBody = null
                else -> throw SerializationException(
                    "Encountered unsupported opcode: $indication"
                )
            }

            ConditionalBlockInstruction(
                type = type,
                ifBody = ifBody,
                elseBody = elseBody
            )
        }
        0x0Cu -> UnconditionalBranchInstruction(source.readVarUInt32())
        0x0Du -> ConditionalBranchInstruction(source.readVarUInt32())
        0x0Eu -> {
            val labelIndices = source.readVarUInt32Vector()
            val defaultLabelIndex = source.readVarUInt32()

            TableBranchInstruction(
                labelIndices = labelIndices,
                defaultLabelIndex = defaultLabelIndex
            )
        }
        0x0Fu -> RETURN
        0x10u -> CallInstruction(source.readVarUInt32())
        0x11u -> {
            val typeIndex = source.readVarUInt32()
            val tableIndex = source.readVarUInt32()

            CallIndirectInstruction(
                typeIndex = typeIndex,
                tableIndex = tableIndex
            )
        }

        0x1Au -> DROP
        0x1Bu -> SELECT
        0x20u -> LocalGetInstruction(source.readVarUInt32())
        0x21u -> LocalSetInstruction(source.readVarUInt32())
        0x22u -> LocalTeeInstruction(source.readVarUInt32())
        0x23u -> GlobalGetInstruction(source.readVarUInt32())
        0x24u -> GlobalSetInstruction(source.readVarUInt32())
        0x25u -> TableGetInstruction(source.readVarUInt32())
        0x26u -> TableSetInstruction(source.readVarUInt32())
        0x28u -> deserialize(source, ::Int32LoadInstruction)
        0x29u -> deserialize(source, ::Int64LoadInstruction)
        0x2Au -> deserialize(source, ::Float32LoadInstruction)
        0x2Bu -> deserialize(source, ::Float64LoadInstruction)
        0x2Cu -> deserialize(source, ::Int32Load8SInstruction)
        0x2Du -> deserialize(source, ::Int32Load8UInstruction)
        0x2Eu -> deserialize(source, ::Int32Load16SInstruction)
        0x2Fu -> deserialize(source, ::Int32Load16UInstruction)
        0x30u -> deserialize(source, ::Int64Load8SInstruction)
        0x31u -> deserialize(source, ::Int64Load8UInstruction)
        0x32u -> deserialize(source, ::Int64Load16SInstruction)
        0x33u -> deserialize(source, ::Int64Load16UInstruction)
        0x34u -> deserialize(source, ::Int64Load32SInstruction)
        0x35u -> deserialize(source, ::Int64Load32UInstruction)
        0x36u -> deserialize(source, ::Int32StoreInstruction)
        0x37u -> deserialize(source, ::Int64StoreInstruction)
        0x38u -> deserialize(source, ::Float32StoreInstruction)
        0x39u -> deserialize(source, ::Float64StoreInstruction)
        0x3Au -> deserialize(source, ::Int32Store8Instruction)
        0x3Bu -> deserialize(source, ::Int32Store16Instruction)
        0x3Cu -> deserialize(source, ::Int64Store8Instruction)
        0x3Du -> deserialize(source, ::Int64Store16Instruction)
        0x3Eu -> deserialize(source, ::Int64Store32Instruction)
        0x3Fu -> {
            source.expect(0)
            MemorySizeInstruction
        }
        0x40u -> {
            source.expect(0)
            MemoryGrowInstruction
        }
        0x41u -> Int32ConstInstruction(source.readVarInt32())
        0x42u -> Int64ConstInstruction(source.readVarInt64())
        0x43u -> Float32ConstInstruction(source.readFloat32())
        0x44u -> Float64ConstInstruction(source.readFloat64())
        0x45u -> INT32_EQZ
        0x46u -> INT32_EQ
        0x47u -> INT32_NE
        0x48u -> INT32_LT_S
        0x49u -> INT32_LT_U
        0x4Au -> INT32_GT_S
        0x4Bu -> INT32_GT_U
        0x4Cu -> INT32_LE_S
        0x4Du -> INT32_LE_U
        0x4Eu -> INT32_GE_S
        0x4Fu -> INT32_GE_U
        0x50u -> INT64_EQZ
        0x51u -> INT64_EQ
        0x52u -> INT64_NE
        0x53u -> INT64_LT_S
        0x54u -> INT64_LT_U
        0x55u -> INT64_GT_S
        0x56u -> INT64_GT_U
        0x57u -> INT64_LE_S
        0x58u -> INT64_LE_U
        0x59u -> INT64_GE_S
        0x5Au -> INT64_GE_U
        0x5Bu -> FLOAT32_EQ
        0x5Cu -> FLOAT32_NE
        0x5Du -> FLOAT32_LT
        0x5Eu -> FLOAT32_GT
        0x5Fu -> FLOAT32_LE
        0x60u -> FLOAT32_GE
        0x61u -> FLOAT64_EQ
        0x62u -> FLOAT64_NE
        0x63u -> FLOAT64_LT
        0x64u -> FLOAT64_GT
        0x65u -> FLOAT64_LE
        0x66u -> FLOAT64_GE
        0x67u -> INT32_CLZ
        0x68u -> INT32_CTZ
        0x69u -> INT32_POPCNT
        0x6Au -> INT32_ADD
        0x6Bu -> INT32_SUB
        0x6Cu -> INT32_MUL
        0x6Du -> INT32_DIV_S
        0x6Eu -> INT32_DIV_U
        0x6Fu -> INT32_REM_S
        0x70u -> INT32_REM_U
        0x71u -> INT32_AND
        0x72u -> INT32_OR
        0x73u -> INT32_XOR
        0x74u -> INT32_SHL
        0x75u -> INT32_SHR_S
        0x76u -> INT32_SHR_U
        0x77u -> INT32_ROTL
        0x78u -> INT32_ROTR
        0x79u -> INT64_CLZ
        0x7Au -> INT64_CTZ
        0x7Bu -> INT64_POPCNT
        0x7Cu -> INT64_ADD
        0x7Du -> INT64_SUB
        0x7Eu -> INT64_MUL
        0x7Fu -> INT64_DIV_S
        0x80u -> INT64_DIV_U
        0x81u -> INT64_REM_S
        0x82u -> INT64_REM_U
        0x83u -> INT64_AND
        0x84u -> INT64_OR
        0x85u -> INT64_XOR
        0x86u -> INT64_SHL
        0x87u -> INT64_SHR_S
        0x88u -> INT64_SHR_U
        0x89u -> INT64_ROTL
        0x8Au -> INT64_ROTR
        0x8Bu -> FLOAT32_ABS
        0x8Cu -> FLOAT32_NEG
        0x8Du -> FLOAT32_CEIL
        0x8Eu -> FLOAT32_FLOOR
        0x8Fu -> FLOAT32_TRUNC
        0x90u -> FLOAT32_NEAREST
        0x91u -> FLOAT32_SQRT
        0x92u -> FLOAT32_ADD
        0x93u -> FLOAT32_SUB
        0x94u -> FLOAT32_MUL
        0x95u -> FLOAT32_DIV
        0x96u -> FLOAT32_MIN
        0x97u -> FLOAT32_MAX
        0x98u -> FLOAT32_COPYSIGN
        0x99u -> FLOAT64_ABS
        0x9Au -> FLOAT64_NEG
        0x9Bu -> FLOAT64_CEIL
        0x9Cu -> FLOAT64_FLOOR
        0x9Du -> FLOAT64_TRUNC
        0x9Eu -> FLOAT64_NEAREST
        0x9Fu -> FLOAT64_SQRT
        0xA0u -> FLOAT64_ADD
        0xA1u -> FLOAT64_SUB
        0xA2u -> FLOAT64_MUL
        0xA3u -> FLOAT64_DIV
        0xA4u -> FLOAT64_MIN
        0xA5u -> FLOAT64_MAX
        0xA6u -> FLOAT64_COPYSIGN
        0xA7u -> INT32_WRAP_INT64
        0xA8u -> INT32_TRUNC_FLOAT32_S
        0xA9u -> INT32_TRUNC_FLOAT32_U
        0xAAu -> INT32_TRUNC_FLOAT64_S
        0xABu -> INT32_TRUNC_FLOAT64_U
        0xACu -> INT64_EXTEND_INT32_S
        0xADu -> INT64_EXTEND_INT32_U
        0xAEu -> INT64_TRUNC_FLOAT32_S
        0xAFu -> INT64_TRUNC_FLOAT32_U
        0xB0u -> INT64_TRUNC_FLOAT64_S
        0xB1u -> INT64_TRUNC_FLOAT64_U
        0xB2u -> FLOAT32_CONVERT_INT32_S
        0xB3u -> FLOAT32_CONVERT_INT32_U
        0xB4u -> FLOAT32_CONVERT_INT64_S
        0xB5u -> FLOAT32_CONVERT_INT64_U
        0xB6u -> FLOAT32_DEMOTE_FLOAT64
        0xB7u -> FLOAT64_CONVERT_INT32_S
        0xB8u -> FLOAT64_CONVERT_INT32_U
        0xB9u -> FLOAT64_CONVERT_INT64_S
        0xBAu -> FLOAT64_CONVERT_INT64_U
        0xBBu -> FLOAT64_PROMOTE_FLOAT32
        0xBCu -> INT32_REINTERPRET_FLOAT32
        0xBDu -> INT64_REINTERPRET_FLOAT64
        0xBEu -> FLOAT32_REINTERPRET_INT32
        0xBFu -> FLOAT64_REINTERPRET_INT64
        0xC0u -> INT32_EXTEND8_S
        0xC1u -> INT32_EXTEND16_S
        0xC2u -> INT64_EXTEND8_S
        0xC3u -> INT64_EXTEND16_S
        0xC4u -> INT64_EXTEND32_S
        0xD1u -> REF_IS_NULL
        0xD2u -> ReferenceFunctionInstruction(source.readVarUInt32())

        else -> throw SerializationException(
            "Encountered unsupported opcode: $opcode"
        )
    }

    private inline fun <TAlignedMemoryInstruction : AlignedMemoryInstruction> deserialize(
        source: ByteSource,
        constructor: (UInt, UInt) -> TAlignedMemoryInstruction
    ): TAlignedMemoryInstruction {
        val alignment = source.readVarUInt32()
        val offset = source.readVarUInt32()

        return constructor(alignment, offset)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun serialize(
        instance: Instruction,
        sink: ByteSink,
        context: Context
    ) = when (instance) {
        is Float32LoadInstruction -> serialize(sink, 0x2Au, instance)
        is Float32StoreInstruction -> serialize(sink, 0x38u, instance)
        is Float64LoadInstruction -> serialize(sink, 0x2Bu, instance)
        is Float64StoreInstruction -> serialize(sink, 0x39u, instance)
        is Int32Load16SInstruction -> serialize(sink, 0x2Eu, instance)
        is Int32Load16UInstruction -> serialize(sink, 0x2Fu, instance)
        is Int32Load8SInstruction -> serialize(sink, 0x2Cu, instance)
        is Int32Load8UInstruction -> serialize(sink, 0x2Du, instance)
        is Int32LoadInstruction -> serialize(sink, 0x28u, instance)
        is Int32Store16Instruction -> serialize(sink, 0x3Bu, instance)
        is Int32Store8Instruction -> serialize(sink, 0x3Au, instance)
        is Int32StoreInstruction -> serialize(sink, 0x36u, instance)
        is Int64Load16SInstruction -> serialize(sink, 0x32u, instance)
        is Int64Load16UInstruction -> serialize(sink, 0x33u, instance)
        is Int64Load32SInstruction -> serialize(sink, 0x34u, instance)
        is Int64Load32UInstruction -> serialize(sink, 0x35u, instance)
        is Int64Load8SInstruction -> serialize(sink, 0x30u, instance)
        is Int64Load8UInstruction -> serialize(sink, 0x31u, instance)
        is Int64LoadInstruction -> serialize(sink, 0x29u, instance)
        is Int64Store16Instruction -> serialize(sink, 0x3Du, instance)
        is Int64Store32Instruction -> serialize(sink, 0x3Eu, instance)
        is Int64Store8Instruction -> serialize(sink, 0x3Cu, instance)
        is Int64StoreInstruction -> serialize(sink, 0x37u, instance)
        is BlockInstruction -> {
            sink.write(0x02)
            context.serialize(sink, instance.type)
            context.serializeVector(sink, instance.body)
            sink.write(0x0B)
        }
        is ConditionalBlockInstruction -> {
            sink.write(0x04)
            context.serialize(sink, instance.type)
            context.serializeVector(sink, instance.ifBody)

            instance.elseBody?.let { elseBody ->
                sink.write(0x05)
                context.serializeVector(sink, elseBody)
            }

            sink.write(0x0B)
        }
        is LoopInstruction -> {
            sink.write(0x03)
            context.serialize(sink, instance.type)
            context.serializeVector(sink, instance.body)
            sink.write(0x0B)
        }
        is CallIndirectInstruction -> {
            sink.write(0x11)
            sink.writeVarUInt32(instance.typeIndex)
            sink.writeVarUInt32(instance.tableIndex)
        }

        is CallInstruction -> serialize(sink, instance.functionIndex, 0x10u)
        is ConditionalBranchInstruction ->
            serialize(sink, instance.labelIndex, 0x0Du)

        is Float32ConstInstruction ->
            serialize(sink, 0x43u, instance, ByteSink::writeFloat32)

        is Float64ConstInstruction ->
            serialize(sink, 0x44u, instance, ByteSink::writeFloat64)

        is Int32ConstInstruction ->
            serialize(sink, 0x41u, instance, ByteSink::writeVarInt32)

        is Int64ConstInstruction ->
            serialize(sink, 0x41u, instance, ByteSink::writeVarInt64)

        is DataDropInstruction ->
            serialize(sink, instance.dataIndex, 0xFCu, 0x09u)

        is ElementDropInstruction ->
            serialize(sink, instance.elementIndex, 0xFCu, 0x0Du)

        is GlobalGetInstruction -> serialize(sink, instance.globalIndex, 0x23u)
        is GlobalSetInstruction -> serialize(sink, instance.globalIndex, 0x24u)
        is LocalGetInstruction -> serialize(sink, instance.localIndex, 0x20u)
        is LocalSetInstruction -> serialize(sink, instance.localIndex, 0x21u)
        is LocalTeeInstruction -> serialize(sink, instance.localIndex, 0x22u)
        MemoryGrowInstruction -> sink.write(0x40, 0x00)
        is MemoryInitInstruction -> {
            sink.write(0xFCu, 0x08u)
            sink.writeVarUInt32(instance.dataIndex)
            sink.write(0x00)
        }

        MemorySizeInstruction -> sink.write(0x3F, 0x00)
        DROP -> sink.write(0x1A)
        FLOAT32_ABS -> sink.write(0x8Bu)
        FLOAT32_ADD -> sink.write(0x92u)
        FLOAT32_CEIL -> sink.write(0x8Du)
        FLOAT32_CONVERT_INT32_S -> sink.write(0xB2u)
        FLOAT32_CONVERT_INT32_U -> sink.write(0xB3u)
        FLOAT32_CONVERT_INT64_S -> sink.write(0xB4u)
        FLOAT32_CONVERT_INT64_U -> sink.write(0xB5u)
        FLOAT32_COPYSIGN -> sink.write(0x98u)
        FLOAT32_DEMOTE_FLOAT64 -> sink.write(0xB6u)
        FLOAT32_DIV -> sink.write(0x95u)
        FLOAT32_EQ -> sink.write(0x5B)
        FLOAT32_FLOOR -> sink.write(0x8Eu)
        FLOAT32_GE -> sink.write(0x60)
        FLOAT32_GT -> sink.write(0x5E)
        FLOAT32_LE -> sink.write(0x5F)
        FLOAT32_LT -> sink.write(0x5D)
        FLOAT32_MAX -> sink.write(0x97u)
        FLOAT32_MIN -> sink.write(0x96u)
        FLOAT32_MUL -> sink.write(0x94u)
        FLOAT32_NE -> sink.write(0x5C)
        FLOAT32_NEAREST -> sink.write(0x90u)
        FLOAT32_NEG -> sink.write(0x8Cu)
        FLOAT32_REINTERPRET_INT32 -> sink.write(0xBEu)
        FLOAT32_SQRT -> sink.write(0x91u)
        FLOAT32_SUB -> sink.write(0x93u)
        FLOAT32_TRUNC -> sink.write(0x8Fu)
        FLOAT64_ABS -> sink.write(0x99u)
        FLOAT64_ADD -> sink.write(0xA0u)
        FLOAT64_CEIL -> sink.write(0x9Bu)
        FLOAT64_CONVERT_INT32_S -> sink.write(0xB7u)
        FLOAT64_CONVERT_INT32_U -> sink.write(0xB8u)
        FLOAT64_CONVERT_INT64_S -> sink.write(0xB9u)
        FLOAT64_CONVERT_INT64_U -> sink.write(0xBAu)
        FLOAT64_COPYSIGN -> sink.write(0xA6u)
        FLOAT64_DIV -> sink.write(0xA3u)
        FLOAT64_EQ -> sink.write(0x61)
        FLOAT64_FLOOR -> sink.write(0x9Cu)
        FLOAT64_GE -> sink.write(0x66)
        FLOAT64_GT -> sink.write(0x64)
        FLOAT64_LE -> sink.write(0x65)
        FLOAT64_LT -> sink.write(0x63)
        FLOAT64_MAX -> sink.write(0xA5u)
        FLOAT64_MIN -> sink.write(0xA4u)
        FLOAT64_MUL -> sink.write(0xA2u)
        FLOAT64_NE -> sink.write(0x62)
        FLOAT64_NEAREST -> sink.write(0x9Eu)
        FLOAT64_NEG -> sink.write(0x9Au)
        FLOAT64_PROMOTE_FLOAT32 -> sink.write(0xBBu)
        FLOAT64_REINTERPRET_INT64 -> sink.write(0xBFu)
        FLOAT64_SQRT -> sink.write(0x9Fu)
        FLOAT64_SUB -> sink.write(0xA1u)
        FLOAT64_TRUNC -> sink.write(0x9Du)
        INT32_ADD -> sink.write(0x6A)
        INT32_AND -> sink.write(0x71)
        INT32_CLZ -> sink.write(0x67)
        INT32_CTZ -> sink.write(0x68)
        INT32_DIV_S -> sink.write(0x6D)
        INT32_DIV_U -> sink.write(0x6E)
        INT32_EQ -> sink.write(0x46)
        INT32_EQZ -> sink.write(0x45)
        INT32_EXTEND8_S -> sink.write(0xC0u)
        INT32_EXTEND16_S -> sink.write(0xC1u)
        INT32_GE_S -> sink.write(0x4E)
        INT32_GE_U -> sink.write(0x4F)
        INT32_GT_S -> sink.write(0x4A)
        INT32_GT_U -> sink.write(0x4B)
        INT32_LE_S -> sink.write(0x4C)
        INT32_LE_U -> sink.write(0x4D)
        INT32_LT_S -> sink.write(0x48)
        INT32_LT_U -> sink.write(0x49)
        INT32_MUL -> sink.write(0x6C)
        INT32_NE -> sink.write(0x47)
        INT32_OR -> sink.write(0x72)
        INT32_POPCNT -> sink.write(0x69)
        INT32_REINTERPRET_FLOAT32 -> sink.write(0xBCu)
        INT32_REM_S -> sink.write(0x6F)
        INT32_REM_U -> sink.write(0x70)
        INT32_ROTL -> sink.write(0x77)
        INT32_ROTR -> sink.write(0x78)
        INT32_SHL -> sink.write(0x74)
        INT32_SHR_S -> sink.write(0x75)
        INT32_SHR_U -> sink.write(0x76)
        INT32_SUB -> sink.write(0x6B)
        INT32_TRUNC_FLOAT32_S -> sink.write(0xA8u)
        INT32_TRUNC_FLOAT32_U -> sink.write(0xA9u)
        INT32_TRUNC_FLOAT64_S -> sink.write(0xAAu)
        INT32_TRUNC_FLOAT64_U -> sink.write(0xABu)
        INT32_WRAP_INT64 -> sink.write(0xA7u)
        INT32_XOR -> sink.write(0x73)
        INT64_ADD -> sink.write(0x7C)
        INT64_AND -> sink.write(0x83u)
        INT64_CLZ -> sink.write(0x79)
        INT64_CTZ -> sink.write(0x7A)
        INT64_DIV_S -> sink.write(0x7F)
        INT64_DIV_U -> sink.write(0x80u)
        INT64_EQ -> sink.write(0x51)
        INT64_EQZ -> sink.write(0x50)
        INT64_EXTEND8_S -> sink.write(0xC2u)
        INT64_EXTEND16_S -> sink.write(0xC3u)
        INT64_EXTEND32_S -> sink.write(0xC4u)
        INT64_EXTEND_INT32_S -> sink.write(0xACu)
        INT64_EXTEND_INT32_U -> sink.write(0xADu)
        INT64_GE_S -> sink.write(0x59)
        INT64_GE_U -> sink.write(0x5A)
        INT64_GT_S -> sink.write(0x55)
        INT64_GT_U -> sink.write(0x56)
        INT64_LE_S -> sink.write(0x57)
        INT64_LE_U -> sink.write(0x58)
        INT64_LT_S -> sink.write(0x53)
        INT64_LT_U -> sink.write(0x54)
        INT64_MUL -> sink.write(0x7E)
        INT64_NE -> sink.write(0x52)
        INT64_OR -> sink.write(0x84u)
        INT64_POPCNT -> sink.write(0x7B)
        INT64_REINTERPRET_FLOAT64 -> sink.write(0xBDu)
        INT64_REM_S -> sink.write(0x81u)
        INT64_REM_U -> sink.write(0x82u)
        INT64_ROTL -> sink.write(0x89u)
        INT64_ROTR -> sink.write(0x8Au)
        INT64_SHL -> sink.write(0x86u)
        INT64_SHR_S -> sink.write(0x87u)
        INT64_SHR_U -> sink.write(0x88u)
        INT64_SUB -> sink.write(0x7D)
        INT64_TRUNC_FLOAT32_S -> sink.write(0xAEu)
        INT64_TRUNC_FLOAT32_U -> sink.write(0xAFu)
        INT64_TRUNC_FLOAT64_S -> sink.write(0xB0u)
        INT64_TRUNC_FLOAT64_U -> sink.write(0xB1u)
        INT64_XOR -> sink.write(0x85u)
        NOP -> sink.write(0x01)
        REF_IS_NULL -> sink.write(0xD1u)
        RETURN -> sink.write(0x0F)
        SELECT -> sink.write(0x1B)
        UNREACHABLE -> sink.write(0x00)
        is ReferenceFunctionInstruction ->
            serialize(sink, instance.functionIndex, 0xD2u)

        is TableBranchInstruction -> {
            sink.write(0x0E)
            sink.writeVarUInt32Vector(instance.labelIndices)
            sink.writeVarUInt32(instance.defaultLabelIndex)
        }
        is TableCopyInstruction -> {
            sink.write(0xFCu, 0x0Eu)
            sink.writeVarUInt32(instance.tableIndex1)
            sink.writeVarUInt32(instance.tableIndex2)
        }
        is TableFillInstruction ->
            serialize(sink, instance.tableIndex, 0xFCu, 0x11u)

        is TableGetInstruction -> serialize(sink, instance.tableIndex, 0x25u)
        is TableGrowInstruction ->
            serialize(sink, instance.tableIndex, 0xFCu, 0x0Fu)

        is TableInitInstruction -> {
            sink.write(0xFCu, 0x0Cu)
            sink.writeVarUInt32(instance.elementIndex)
            sink.writeVarUInt32(instance.tableIndex)
        }
        is TableSetInstruction -> serialize(sink, instance.tableIndex, 0x26u)
        is TableSizeInstruction ->
            serialize(sink, instance.tableIndex, 0xFCu, 0x10u)

        is UnconditionalBranchInstruction ->
            serialize(sink, instance.labelIndex, 0x0Cu)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun serialize(
        sink: ByteSink,
        opcode: UByte,
        instruction: AlignedMemoryInstruction
    ) {
        sink.write(opcode)
        sink.writeVarUInt32(instruction.alignment)
        sink.writeVarUInt32(instruction.offset)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private inline fun <TValue> serialize(
        sink: ByteSink,
        opcode: UByte,
        instruction: ConstInstruction<TValue>,
        writeFunction: ByteSink.(value: TValue) -> Unit
    ) {
        sink.write(opcode)
        sink.writeFunction(instruction.constant)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun serialize(
        sink: ByteSink,
        parameter: UInt,
        vararg opcode: UByte
    ) {
        sink.write(*opcode)
        sink.writeVarUInt32(parameter)
    }
}
