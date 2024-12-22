package dev.fir3.iwan.io.wasm.serialization.valueTypes

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.serialization.SerializationStrategy
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.write
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.wasm.models.valueTypes.NumberType
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType
import dev.fir3.iwan.io.wasm.models.valueTypes.ValueType
import dev.fir3.iwan.io.wasm.models.valueTypes.VectorType
import java.io.IOException

internal object ValueTypeStrategy :
    DeserializationStrategy<ValueType>,
    SerializationStrategy<ValueType> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): ValueType = when (val typeId = source.readInt8()) {
        ValueTypeIds.EXTERNAL_REFERENCE -> ReferenceType.ExternalReference
        ValueTypeIds.FLOAT32 -> NumberType.Float32
        ValueTypeIds.FLOAT64 -> NumberType.Float64
        ValueTypeIds.FUNCTION_REFERENCE -> ReferenceType.FunctionReference
        ValueTypeIds.INT32 -> NumberType.Int32
        ValueTypeIds.INT64 -> NumberType.Int64
        ValueTypeIds.VECTOR128 -> VectorType.Vector128
        else -> throw IOException("Unsupported value type id '${typeId}'")
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: ValueType
    ) = when (value) {
        NumberType.Float32 -> sink.write(ValueTypeIds.FLOAT32)
        NumberType.Float64 -> sink.write(ValueTypeIds.FLOAT64)
        NumberType.Int32 -> sink.write(ValueTypeIds.INT32)
        NumberType.Int64 -> sink.write(ValueTypeIds.INT64)
        ReferenceType.ExternalReference ->
            sink.write(ValueTypeIds.EXTERNAL_REFERENCE)

        ReferenceType.FunctionReference ->
            sink.write(ValueTypeIds.FUNCTION_REFERENCE)

        VectorType.Vector128 -> sink.write(ValueTypeIds.VECTOR128)
    }
}
