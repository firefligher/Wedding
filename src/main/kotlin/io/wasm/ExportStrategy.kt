package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.*
import dev.fir3.wedding.io.foundation.readInt8
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.wasm.*

object ExportStrategy : Strategy<Export> {
    override fun deserialize(source: ByteSource, context: Context): Export {
        val name = source.readName()

        return when (val typeId = source.readInt8().toInt()) {
            0x00 -> FunctionExport(source.readVarUInt32(), name)
            0x03 -> GlobalExport(source.readVarUInt32(), name)
            0x02 -> MemoryExport(source.readVarUInt32(), name)
            0x01 -> TableExport(name, source.readVarUInt32())
            else -> throw IOException("Invalid typeId '$typeId'")
        }
    }

    override fun serialize(
        instance: Export,
        sink: ByteSink,
        context: Context
    ) {
        sink.writeName(instance.name)

        when (instance) {
            is FunctionExport -> {
                sink.write(0x00)
                sink.writeVarUInt32(instance.functionIndex)
            }
            is GlobalExport -> {
                sink.write(0x03)
                sink.writeVarUInt32(instance.globalIndex)
            }
            is MemoryExport -> {
                sink.write(0x02)
                sink.writeVarUInt32(instance.memoryIndex)
            }
            is TableExport -> {
                sink.write(0x01)
                sink.writeVarUInt32(instance.tableIndex)
            }
        }
    }
}
