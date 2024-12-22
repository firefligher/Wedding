package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.source.readName
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.*
import java.io.IOException

internal object ExportStrategy : DeserializationStrategy<Export> {
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
}