package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.*
import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.serialization.SerializationStrategy
import dev.fir3.iwan.io.serialization.deserializeVector
import dev.fir3.iwan.io.sink.*
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.createMemoryByteSink
import dev.fir3.iwan.io.sink.write
import dev.fir3.iwan.io.sink.writeVarUInt32
import dev.fir3.iwan.io.source.*
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.sections.*
import java.io.IOException

internal object SectionStrategy :
    DeserializationStrategy<Section>,
    SerializationStrategy<Section> {
    private const val CODE_SECTION: Byte = 0x0A
    private const val CUSTOM_SECTION: Byte = 0x00
    private const val DATA_SECTION: Byte = 0x0B
    private const val ELEMENT_SECTION: Byte = 0x09
    private const val EXPORT_SECTION: Byte = 0x07
    private const val FUNCTION_SECTION: Byte = 0x03
    private const val GLOBAL_SECTION: Byte = 0x06
    private const val IMPORT_SECTION: Byte = 0x02
    private const val MEMORY_SECTION: Byte = 0x05
    private const val START_SECTION: Byte = 0x08
    private const val TABLE_SECTION: Byte = 0x04
    private const val TYPE_SECTION: Byte = 0x01

    private inline fun <TSection : Section> build(
        source: ByteSource,
        context: DeserializationContext,
        factory: (ByteSource, DeserializationContext, UInt) -> TSection
    ) = factory(source, context, source.readVarUInt32())

    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ) = when (val sectionId = source.readInt8()) {
        CODE_SECTION -> build(source, context) { src, ctx, _ ->
            CodeSection(ctx.deserializeVector(src))
        }

        CUSTOM_SECTION -> build(source, context) { src, _, sectionSize ->
            // Read custom section name

            val nameSize = src.readVarUInt32()

            if (nameSize > Int.MAX_VALUE.toUInt()) {
                throw IOException(
                    "Unsupported length of custom section name"
                )
            }

            val name = String(src.read(nameSize.toInt()))

            // Read content

            val contentSize = sectionSize - nameSize

            if (contentSize > Int.MAX_VALUE.toUInt()) {
                throw IOException(
                    "Unsupported length of custom section content"
                )
            }

            val content = src.read(contentSize.toInt())

            // Instance construction

            CustomSection(name, content)
        }

        DATA_SECTION -> build(source, context) { src, ctx, _ ->
            DataSection(ctx.deserializeVector(src))
        }

        ELEMENT_SECTION -> build(source, context) { src, ctx, _ ->
            ElementSection(ctx.deserializeVector(src))
        }

        EXPORT_SECTION -> build(source, context) { src, ctx, _ ->
            ExportSection(ctx.deserializeVector(src))
        }

        FUNCTION_SECTION -> build(source, context) { src, _, _ ->
            val typeCount = src.readVarUInt32()
            val types = mutableListOf<UInt>()

            for (i in 0u until typeCount) {
                types.add(src.readVarUInt32())
            }

            FunctionSection(types)
        }

        GLOBAL_SECTION -> build(source, context) { src, ctx, _ ->
            GlobalSection(ctx.deserializeVector(src))
        }

        IMPORT_SECTION -> build(source, context) { src, ctx, _ ->
            ImportSection(ctx.deserializeVector(src))
        }

        MEMORY_SECTION -> build(source, context) { src, ctx, _ ->
            MemorySection(ctx.deserializeVector(src))
        }

        START_SECTION -> build(source, context) { src, _, _ ->
            StartSection(src.readVarUInt32())
        }

        TABLE_SECTION -> build(source, context) { src, ctx, _ ->
            TableSection(ctx.deserializeVector(src))
        }

        TYPE_SECTION -> build(source, context) { src, ctx, _ ->
            TypeSection(ctx.deserializeVector(src))
        }

        else -> throw IOException("Unsupported section id '$sectionId'")
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: Section
    ) {
        val tempSink = createMemoryByteSink()

        when (value) {
            is CodeSection -> {
                sink.write(CODE_SECTION)
                context.serializeVector(tempSink, value.codes)
            }
            is CustomSection -> TODO()
            is DataSection -> {
                sink.write(DATA_SECTION)
                context.serializeVector(tempSink, value.data)
            }
            is ElementSection -> {
                sink.write(ELEMENT_SECTION)
                context.serializeVector(tempSink, value.elements)
            }
            is ExportSection -> {
                sink.write(EXPORT_SECTION)
                context.serializeVector(tempSink, value.exports)
            }
            is FunctionSection -> {
                sink.write(FUNCTION_SECTION)
                tempSink.writeVarUInt32(value.types.size.toUInt())
                value.types.forEach(tempSink::writeVarUInt32)
            }
            is GlobalSection -> {
                sink.write(GLOBAL_SECTION)
                context.serializeVector(tempSink, value.globals)
            }
            is ImportSection -> {
                sink.write(IMPORT_SECTION)
                context.serializeVector(tempSink, value.imports)
            }
            is MemorySection -> {
                sink.write(MEMORY_SECTION)
                context.serializeVector(tempSink, value.memories)
            }
            is StartSection -> {
                sink.write(START_SECTION)
                tempSink.writeVarUInt32(value.function)
            }
            is TableSection -> {
                sink.write(TABLE_SECTION)
                context.serializeVector(tempSink, value.types)
            }
            is TypeSection -> {
                sink.write(TYPE_SECTION)
                context.serializeVector(tempSink, value.types)
            }
        }

        sink.writeVarUInt32(tempSink.buffer.size.toUInt())
        sink.write(*tempSink.buffer)
    }
}
