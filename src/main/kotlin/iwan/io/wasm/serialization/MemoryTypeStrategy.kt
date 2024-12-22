package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.MemoryType
import java.io.IOException

internal object MemoryTypeStrategy : DeserializationStrategy<MemoryType> {
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
}
