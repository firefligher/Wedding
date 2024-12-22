package dev.fir3.iwan.io.wasm.serialization.valueTypes

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.wasm.models.valueTypes.NumberType
import java.io.IOException

internal object NumberTypeStrategy : DeserializationStrategy<NumberType> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ) = when (val typeId = source.readInt8()) {
        ValueTypeIds.FLOAT32 -> NumberType.Float32
        ValueTypeIds.FLOAT64 -> NumberType.Float64
        ValueTypeIds.INT32 -> NumberType.Int32
        ValueTypeIds.INT64 -> NumberType.Int64
        else -> throw IOException("Unsupported number type id '${typeId}'")
    }
}
