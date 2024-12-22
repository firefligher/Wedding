package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.serialization.SerializationStrategy
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.write
import dev.fir3.iwan.io.sink.writeVarUInt32
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.MemoryType
import java.io.IOException

internal object MemoryTypeStrategy :
    DeserializationStrategy<MemoryType>,
    SerializationStrategy<MemoryType> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): MemoryType {
        val prefix = source.readInt8()

        if (prefix == LimitPrefix.HAS_NO_MAXIMUM) {
            return MemoryType(source.readVarUInt32(), null)
        }

        if (prefix == LimitPrefix.HAS_MAXIMUM) {
            val minimum = source.readVarUInt32()
            val maximum = source.readVarUInt32()

            return MemoryType(minimum, maximum)
        }

        throw IOException("Encountered undefined limit prefix")
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: MemoryType
    ) {
        sink.write(
            value.maximum?.let { LimitPrefix.HAS_MAXIMUM }
                ?: LimitPrefix.HAS_NO_MAXIMUM
        )

        sink.writeVarUInt32(value.minimum)
        value.maximum?.let(sink::writeVarUInt32)
    }
}
