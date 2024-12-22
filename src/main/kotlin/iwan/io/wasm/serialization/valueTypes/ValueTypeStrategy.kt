package dev.fir3.iwan.io.wasm.serialization.valueTypes

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.wasm.models.valueTypes.NumberType
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType
import dev.fir3.iwan.io.wasm.models.valueTypes.ValueType
import dev.fir3.iwan.io.wasm.models.valueTypes.VectorType
import java.io.IOException

internal object ValueTypeStrategy : DeserializationStrategy<ValueType> {
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
}
