package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.*
import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.serialization.SerializationStrategy
import dev.fir3.iwan.io.serialization.deserialize
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.write
import dev.fir3.iwan.io.sink.writeVarUInt32
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.TableType
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType
import java.io.IOException

internal object TableTypeStrategy :
    DeserializationStrategy<TableType>,
    SerializationStrategy<TableType> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): TableType {
        val elementType = context.deserialize<ReferenceType>(source)
        val limitPrefix = source.readInt8()
        val minimum = source.readVarUInt32()

        val maximum = when (limitPrefix) {
            LimitPrefix.HAS_NO_MAXIMUM -> null
            LimitPrefix.HAS_MAXIMUM -> source.readVarUInt32()
            else -> throw IOException("Invalid limit prefix")
        }

        return TableType(elementType, minimum, maximum)
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: TableType
    ) {
        context.serialize(sink, value.elementType)
        sink.write(
            value.maximum?.let { LimitPrefix.HAS_MAXIMUM }
                ?: LimitPrefix.HAS_NO_MAXIMUM
        )

        sink.writeVarUInt32(value.minimum)
        value.maximum?.let(sink::writeVarUInt32)
    }
}
