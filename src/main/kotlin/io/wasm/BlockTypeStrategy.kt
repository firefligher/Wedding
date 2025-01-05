package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.SerializationException
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.wasm.*

object BlockTypeStrategy : Strategy<BlockType> {
    private val EMPTY_ID = maskValueTypeId(0x40)
    private val EXTERNAL_REFERENCE_ID = maskValueTypeId(0x6F)
    private val FLOAT32_ID = maskValueTypeId(0x7D)
    private val FLOAT64_ID = maskValueTypeId(0x7C)
    private val FUNCTION_REFERENCE_ID = maskValueTypeId(0x70)
    private val INT32_ID = maskValueTypeId(0x7F)
    private val INT64_ID = maskValueTypeId(0x7E)
    private val VECTOR128_ID = maskValueTypeId(0x7B)

    private fun maskValueTypeId(id: Byte) =
        (0xFFFFFF80u or id.toUInt()).toInt()

    @Throws(SerializationException::class)
    override fun deserialize(
        source: ByteSource,
        context: Context
    ) = when (val typeId = source.readVarInt32()) {
        EMPTY_ID -> EmptyBlockType
        EXTERNAL_REFERENCE_ID -> InlineBlockType(ReferenceType.EXTERNAL)
        FLOAT32_ID -> InlineBlockType(NumberType.FLOAT32)
        FLOAT64_ID -> InlineBlockType(NumberType.FLOAT64)
        FUNCTION_REFERENCE_ID -> InlineBlockType(ReferenceType.FUNCTION)
        INT32_ID -> InlineBlockType(NumberType.INT32)
        INT64_ID -> InlineBlockType(NumberType.INT64)
        VECTOR128_ID -> InlineBlockType(VectorType.V128)

        else -> {
            if (typeId < 0) {
                throw SerializationException("Unknown block type '$typeId'")
            }

            FunctionBlockType(typeId.toUInt())
        }
    }

    override fun serialize(
        instance: BlockType,
        sink: ByteSink,
        context: Context
    ) = when (instance) {
        EmptyBlockType -> sink.writeVarInt32(EMPTY_ID)
        is FunctionBlockType -> sink.writeVarUInt32(instance.value)
        is InlineBlockType -> when (instance.valueType) {
            NumberType.FLOAT32 -> sink.writeVarInt32(FLOAT32_ID)
            NumberType.FLOAT64 -> sink.writeVarInt32(FLOAT64_ID)
            NumberType.INT32 -> sink.writeVarInt32(INT32_ID)
            NumberType.INT64 -> sink.writeVarInt32(INT64_ID)
            ReferenceType.EXTERNAL ->
                sink.writeVarInt32(EXTERNAL_REFERENCE_ID)

            ReferenceType.FUNCTION ->
                sink.writeVarInt32(FUNCTION_REFERENCE_ID)

            VectorType.V128 -> sink.writeVarInt32(VECTOR128_ID)
        }
    }
}
