package dev.fir3.iwan.io.wasm.serialization.valueTypes

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.expectInt8
import dev.fir3.iwan.io.wasm.models.valueTypes.VectorType
import java.io.IOException

internal object VectorTypeStrategy : DeserializationStrategy<VectorType> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): VectorType {
        source.expectInt8(ValueTypeIds.VECTOR128)
        return VectorType.Vector128
    }
}
