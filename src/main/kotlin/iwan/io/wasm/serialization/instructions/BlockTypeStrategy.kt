package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.serialization.SerializationStrategy
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.writeVarInt32
import dev.fir3.iwan.io.sink.writeVarUInt32
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readVarInt32
import dev.fir3.iwan.io.wasm.models.instructions.blockTypes.BlockType
import dev.fir3.iwan.io.wasm.models.instructions.blockTypes.EmptyBlockType
import dev.fir3.iwan.io.wasm.models.instructions.blockTypes.FunctionBlockType
import dev.fir3.iwan.io.wasm.models.instructions.blockTypes.InlineBlockType
import dev.fir3.iwan.io.wasm.models.valueTypes.NumberType
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType
import dev.fir3.iwan.io.wasm.models.valueTypes.VectorType
import dev.fir3.iwan.io.wasm.serialization.valueTypes.ValueTypeIds
import java.io.IOException

internal object BlockTypeStrategy :
    DeserializationStrategy<BlockType>,
    SerializationStrategy<BlockType> {
    private val EMPTY_ID = maskValueTypeId(0x40)
    private val EXTERNAL_REFERENCE_ID =
        maskValueTypeId(ValueTypeIds.EXTERNAL_REFERENCE)

    private val FLOAT32_ID = maskValueTypeId(ValueTypeIds.FLOAT32)
    private val FLOAT64_ID = maskValueTypeId(ValueTypeIds.FLOAT64)
    private val FUNCTION_REFERENCE_ID =
        maskValueTypeId(ValueTypeIds.FUNCTION_REFERENCE)

    private val INT32_ID = maskValueTypeId(ValueTypeIds.INT32)
    private val INT64_ID = maskValueTypeId(ValueTypeIds.INT64)
    private val VECTOR128_ID = maskValueTypeId(ValueTypeIds.VECTOR128)

    private fun maskValueTypeId(id: Byte) =
        (0xFFFFFF80u or id.toUInt()).toInt()

    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ) = when (val typeId = source.readVarInt32()) {
        EMPTY_ID -> EmptyBlockType
        EXTERNAL_REFERENCE_ID ->
            InlineBlockType(ReferenceType.ExternalReference)

        FLOAT32_ID -> InlineBlockType(NumberType.Float32)
        FLOAT64_ID -> InlineBlockType(NumberType.Float64)
        FUNCTION_REFERENCE_ID ->
            InlineBlockType(ReferenceType.FunctionReference)

        INT32_ID -> InlineBlockType(NumberType.Int32)
        INT64_ID -> InlineBlockType(NumberType.Int64)
        VECTOR128_ID -> InlineBlockType(VectorType.Vector128)

        else -> {
            if (typeId < 0) {
                throw IOException("Invalid block type '$typeId'")
            }

            FunctionBlockType(typeId.toUInt())
        }
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: BlockType
    ) = when (value) {
        EmptyBlockType -> sink.writeVarInt32(EMPTY_ID)
        is FunctionBlockType -> sink.writeVarUInt32(value.value)
        is InlineBlockType -> when (value.valueType) {
            NumberType.Float32 -> sink.writeVarInt32(FLOAT32_ID)
            NumberType.Float64 -> sink.writeVarInt32(FLOAT64_ID)
            NumberType.Int32 -> sink.writeVarInt32(INT32_ID)
            NumberType.Int64 -> sink.writeVarInt32(INT64_ID)
            ReferenceType.ExternalReference ->
                sink.writeVarInt32(EXTERNAL_REFERENCE_ID)

            ReferenceType.FunctionReference ->
                sink.writeVarInt32(FUNCTION_REFERENCE_ID)

            VectorType.Vector128 -> sink.writeVarInt32(VECTOR128_ID)
        }
    }
}
