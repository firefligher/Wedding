package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.foundation.readUInt8
import dev.fir3.wedding.io.foundation.write
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.SerializationException
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.wasm.NumberType
import dev.fir3.wedding.wasm.ReferenceType
import dev.fir3.wedding.wasm.ValueType
import dev.fir3.wedding.wasm.VectorType

object ValueTypeStrategy : Strategy<ValueType> {
    override fun deserialize(
        source: ByteSource,
        context: Context
    ): ValueType = when (val type = source.readUInt8().toUInt()) {
        0x6Fu -> ReferenceType.EXTERNAL
        0x70u -> ReferenceType.FUNCTION
        0x7Bu -> VectorType.V128
        0x7Cu -> NumberType.FLOAT64
        0x7Du -> NumberType.FLOAT32
        0x7Eu -> NumberType.INT64
        0x7Fu -> NumberType.INT32
        else -> throw SerializationException("Unsupported value type: $type")
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun serialize(
        instance: ValueType,
        sink: ByteSink,
        context: Context
    ) = when (instance) {
        NumberType.FLOAT32 -> sink.write(0x7Du)
        NumberType.FLOAT64 -> sink.write(0x7Cu)
        NumberType.INT32 -> sink.write(0x7Fu)
        NumberType.INT64 -> sink.write(0x7Eu)
        ReferenceType.EXTERNAL -> sink.write(0x6Fu)
        ReferenceType.FUNCTION -> sink.write(0x70u)
        VectorType.V128 -> sink.write(0x7Bu)
    }
}
