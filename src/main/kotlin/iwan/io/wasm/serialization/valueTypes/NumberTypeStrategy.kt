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
import java.io.IOException

internal object NumberTypeStrategy :
    DeserializationStrategy<NumberType>,
    SerializationStrategy<NumberType> {
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

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: NumberType
    ) = when (value) {
        NumberType.Float32 -> sink.write(ValueTypeIds.FLOAT32)
        NumberType.Float64 -> sink.write(ValueTypeIds.FLOAT64)
        NumberType.Int32 -> sink.write(ValueTypeIds.INT32)
        NumberType.Int64 -> sink.write(ValueTypeIds.INT64)
    }
}
