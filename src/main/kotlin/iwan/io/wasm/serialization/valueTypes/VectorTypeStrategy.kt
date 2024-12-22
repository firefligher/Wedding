package dev.fir3.iwan.io.wasm.serialization.valueTypes

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.serialization.SerializationStrategy
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.write
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.expectInt8
import dev.fir3.iwan.io.wasm.models.valueTypes.VectorType
import java.io.IOException

internal object VectorTypeStrategy :
    DeserializationStrategy<VectorType>,
    SerializationStrategy<VectorType> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): VectorType {
        source.expectInt8(ValueTypeIds.VECTOR128)
        return VectorType.Vector128
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: VectorType
    ) = when (value) {
        VectorType.Vector128 -> sink.write(ValueTypeIds.VECTOR128)
    }
}
