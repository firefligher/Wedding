package dev.fir3.iwan.io.wasm.serialization.valueTypes

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.serialization.SerializationStrategy
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.write
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType
import java.io.IOException

internal object ReferenceTypeStrategy :
    DeserializationStrategy<ReferenceType>,
    SerializationStrategy<ReferenceType> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ) = when (val typeId = source.readInt8()) {
        ValueTypeIds.EXTERNAL_REFERENCE -> ReferenceType.ExternalReference
        ValueTypeIds.FUNCTION_REFERENCE -> ReferenceType.FunctionReference
        else -> throw IOException("Unsupported reference type id '${typeId}'")
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: ReferenceType
    ) = when (value) {
        ReferenceType.ExternalReference ->
            sink.write(ValueTypeIds.EXTERNAL_REFERENCE)

        ReferenceType.FunctionReference ->
            sink.write(ValueTypeIds.FUNCTION_REFERENCE)
    }
}
