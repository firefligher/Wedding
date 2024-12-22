package dev.fir3.iwan.io.wasm.serialization.valueTypes

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType
import java.io.IOException

internal object ReferenceTypeStrategy :
    DeserializationStrategy<ReferenceType> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ) = when (val typeId = source.readInt8()) {
        ValueTypeIds.EXTERNAL_REFERENCE -> ReferenceType.ExternalReference
        ValueTypeIds.FUNCTION_REFERENCE -> ReferenceType.FunctionReference
        else -> throw IOException("Unsupported reference type id '${typeId}'")
    }
}
