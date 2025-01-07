package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.foundation.readUInt8
import dev.fir3.wedding.io.foundation.write
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.SerializationException
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.wasm.ReferenceType

object ReferenceTypeStrategy : Strategy<ReferenceType> {
    override fun deserialize(
        source: ByteSource,
        context: Context
    ) = when (val type = source.readUInt8().toUInt()) {
        0x6Fu -> ReferenceType.EXTERNAL
        0x70u -> ReferenceType.FUNCTION
        else -> throw SerializationException(
            "Unsupported reference type: $type"
        )
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun serialize(
        instance: ReferenceType,
        sink: ByteSink,
        context: Context
    ) = when (instance) {
        ReferenceType.EXTERNAL -> sink.write(0x6Fu)
        ReferenceType.FUNCTION -> sink.write(0x70u)
    }
}
