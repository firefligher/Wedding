package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.foundation.readUInt8
import dev.fir3.wedding.io.foundation.write
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.SerializationException
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.wasm.Limits

object LimitsStrategy : Strategy<Limits> {
    override fun deserialize(source: ByteSource, context: Context): Limits {
        val minimum: UInt
        val maximum: UInt?

        when (val type = source.readUInt8().toUInt()) {
            0x00u -> {
                minimum = source.readVarUInt32()
                maximum = null
            }

            0x01u -> {
                minimum = source.readVarUInt32()
                maximum = source.readVarUInt32()
            }

            else -> throw SerializationException(
                "Unsupported limit type: $type"
            )
        }

        return Limits(minimum, maximum)
    }

    override fun serialize(
        instance: Limits,
        sink: ByteSink,
        context: Context
    ) {
        if (instance.maximum == null) {
            sink.write(0x00)
            sink.writeVarUInt32(instance.minimum)
        } else {
            sink.write(0x01)
            sink.writeVarUInt32(instance.minimum)
            sink.writeVarUInt32(instance.maximum)
        }
    }
}
