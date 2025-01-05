package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.foundation.readInt8
import dev.fir3.wedding.io.foundation.write
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.SerializationException
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.io.serialization.deserialize
import dev.fir3.wedding.wasm.*

object ImportStrategy : Strategy<Import> {
    override fun deserialize(source: ByteSource, context: Context): Import {
        val module = source.readName()
        val name = source.readName()

        return when (val typeId = source.readInt8().toInt()) {
            0x00 -> FunctionImport(
                module,
                name,
                source.readVarUInt32()
            )

            0x03 -> {
                val type = context.deserialize<ValueType>(source)
                val isMutable = source.readInt8() == 1.toByte()

                GlobalImport(isMutable, module, name, type)
            }

            0x02 -> MemoryImport(
                context.deserialize(source),
                module,
                name
            )

            0x01 -> TableImport(module, name, context.deserialize(source))
            else -> throw SerializationException("Unsupported typeId '$typeId'")
        }
    }

    override fun serialize(
        instance: Import,
        sink: ByteSink,
        context: Context
    ) {
        sink.writeName(instance.module)
        sink.writeName(instance.name)

        when (instance) {
            is FunctionImport -> {
                sink.write(0x00)
                sink.writeVarUInt32(instance.typeIndex)
            }
            is GlobalImport -> {
                sink.write(0x03)
                context.serialize(sink, instance.type)
                sink.write(
                    if (instance.isMutable) 1
                    else 0
                )
            }
            is MemoryImport -> {
                sink.write(0x02)
                context.serialize(sink, instance.limits)
            }
            is TableImport -> {
                sink.write(0x01)
                context.serialize(sink, instance.type)
            }
        }
    }
}