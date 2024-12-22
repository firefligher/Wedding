package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.serialization.SerializationStrategy
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

internal object ExportStrategy :
    DeserializationStrategy<Export>,
    SerializationStrategy<Export> {
    private const val FUNCTION_ID: Byte = 0x00
    private const val GLOBAL_ID: Byte = 0x03
    private const val MEMORY_ID: Byte = 0x02
    private const val TABLE_ID: Byte = 0x01

    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): Export {
        val name = source.readName()

        return when (val typeId = source.readInt8()) {
            FUNCTION_ID -> FunctionExport(name, source.readVarUInt32())
            GLOBAL_ID -> GlobalExport(name, source.readVarUInt32())
            MEMORY_ID -> MemoryExport(name, source.readVarUInt32())
            TABLE_ID -> TableExport(name, source.readVarUInt32())
            else -> throw IOException("Invalid typeId '$typeId'")
        }
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: Export
    ) {
        sink.writeName(value.name)

        when (value) {
            is FunctionExport -> {
                sink.write(FUNCTION_ID)
                sink.writeVarUInt32(value.functionIndex)
            }
            is GlobalExport -> {
                sink.write(GLOBAL_ID)
                sink.writeVarUInt32(value.globalIndex)
            }
            is MemoryExport -> {
                sink.write(MEMORY_ID)
                sink.writeVarUInt32(value.memoryIndex)
            }
            is TableExport -> {
                sink.write(TABLE_ID)
                sink.writeVarUInt32(value.tableIndex)
            }
        }
    }
}