package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.*
import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.serialization.SerializationStrategy
import dev.fir3.iwan.io.serialization.deserialize
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.write
import dev.fir3.iwan.io.sink.writeName
import dev.fir3.iwan.io.sink.writeVarUInt32
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.source.readName
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.*
import java.io.IOException

internal object ImportStrategy :
    DeserializationStrategy<Import>,
    SerializationStrategy<Import> {
    private const val FUNCTION_ID: Byte = 0x00
    private const val GLOBAL_ID: Byte = 0x03
    private const val MEMORY_ID: Byte = 0x02
    private const val TABLE_ID: Byte = 0x01

    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): Import {
        val module = source.readName()
        val name = source.readName()

        return when (val typeId = source.readInt8()) {
            FUNCTION_ID -> FunctionImport(
                module,
                name,
                source.readVarUInt32()
            )

            GLOBAL_ID -> GlobalImport(
                module,
                name,
                context.deserialize(source)
            )

            MEMORY_ID -> MemoryImport(
                module,
                name,
                context.deserialize(source)
            )

            TABLE_ID -> TableImport(module, name, context.deserialize(source))
            else -> throw IOException("Invalid typeId '$typeId'")
        }
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: Import
    ) {
        sink.writeName(value.module)
        sink.writeName(value.name)

        when (value) {
            is FunctionImport -> {
                sink.write(FUNCTION_ID)
                sink.writeVarUInt32(value.typeIndex)
            }
            is GlobalImport -> {
                sink.write(GLOBAL_ID)
                context.serialize(sink, value.type)
            }
            is MemoryImport -> {
                sink.write(MEMORY_ID)
                context.serialize(sink, value.type)
            }
            is TableImport -> {
                sink.write(TABLE_ID)
                context.serialize(sink, value.type)
            }
        }
    }
}