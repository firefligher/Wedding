package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.*
import dev.fir3.wedding.io.foundation.readUInt8
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.SerializationException
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.wasm.ActiveData
import dev.fir3.wedding.wasm.Data
import dev.fir3.wedding.wasm.Instruction
import dev.fir3.wedding.wasm.PassiveData

object DataStrategy : Strategy<Data> {
    override fun deserialize(
        source: ByteSource,
        context: Context
    ) = when (val type = source.readUInt8().toUInt()) {
        0u -> {
            val offset = context.deserializeVector<Instruction>(source)
            val initializers = source.readInt8Vector()
            ActiveData(initializers, 0u, offset)
        }

        1u -> PassiveData(source.readInt8Vector())
        2u -> {
            val index = source.readVarUInt32()
            val offset = context.deserializeVector<Instruction>(source)
            val initializers = source.readInt8Vector()
            ActiveData(initializers, index, offset)
        }

        else -> throw SerializationException("Unknown data type: $type")
    }

    override fun serialize(
        instance: Data,
        sink: ByteSink,
        context: Context
    ) = when (instance) {
        is ActiveData -> {
            if (instance.memoryIndex > 0u) {
                sink.write(2)
                sink.writeVarUInt32(instance.memoryIndex)
            } else {
                sink.write(0)
            }

            context.serialize(sink, instance.offset)
            sink.writeInt8Vector(instance.initializers)
        }

        is PassiveData -> sink.writeInt8Vector(instance.initializers)
    }
}
