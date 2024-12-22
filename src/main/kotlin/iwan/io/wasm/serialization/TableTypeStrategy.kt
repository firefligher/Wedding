package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.deserialize
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.TableType
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType
import java.io.IOException

internal object TableTypeStrategy : DeserializationStrategy<TableType> {
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
}
