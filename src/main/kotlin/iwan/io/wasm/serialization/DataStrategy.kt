package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.*
import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.serialization.SerializationStrategy
import dev.fir3.iwan.io.serialization.deserialize
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.write
import dev.fir3.iwan.io.sink.writeVarUInt32
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.*
import java.io.IOException

internal object DataStrategy :
    DeserializationStrategy<Data>,
    SerializationStrategy<Data> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ) = when (val typeId = source.readInt8()) {
        0.toByte() -> {
            val e = context.deserialize<Expression>(source)
            val bCount = source.readVarUInt32()
            val b = mutableListOf<Byte>()

            for (i in 0u until bCount) {
                b.add(source.readInt8())
            }

            ActiveData(b, 0u, e)
        }

        1.toByte() -> {
            val bCount = source.readVarUInt32()
            val b = mutableListOf<Byte>()

            for (i in 0u until bCount) {
                b.add(source.readInt8())
            }

            PassiveData(b)
        }

        2.toByte() -> {
            val x = source.readVarUInt32()
            val e = context.deserialize<Expression>(source)
            val bCount = source.readVarUInt32()
            val b = mutableListOf<Byte>()

            for (i in 0u until bCount) {
                b.add(source.readInt8())
            }

            ActiveData(b, x, e)
        }

        else -> throw IOException("Invalid data type '$typeId'")
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: Data
    ) = when (value) {
        is ActiveData -> {
            if (value.memoryIndex > 0u) {
                sink.write(2)
                sink.writeVarUInt32(value.memoryIndex)
            } else {
                sink.write(0)
            }

            context.serialize(sink, value.offset)
            sink.writeVarUInt32(value.initializers.size.toUInt())
            value.initializers.forEach(sink::write)
        }

        is PassiveData -> {
            sink.writeVarUInt32(value.initializers.size.toUInt())
            value.initializers.forEach(sink::write)
        }
    }
}
