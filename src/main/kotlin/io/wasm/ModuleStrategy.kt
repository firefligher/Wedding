package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.cli.Out
import dev.fir3.wedding.io.foundation.*
import dev.fir3.wedding.io.foundation.readUInt8
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.SerializationException
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.wasm.*

object ModuleStrategy : Strategy<Module> {
    override fun deserialize(source: ByteSource, context: Context): Module {
        source.expect(
            0x00, 0x61, 0x73, 0x6D, // File Magic
            0x01, 0x00, 0x00, 0x00  // Version
        )

        val builder = ModuleBuilder()

        while (true) {
            val sectionType = try {
                source.readUInt8()
            } catch (exception: IOException) {
                break
            }

            val sectionSize = source.readVarUInt32()

            when (sectionType.toUInt()) {
                0x0u -> {
                    val countingSource = CountingByteSource(source)
                    val name = countingSource.readName()
                    val remainingBytes = sectionSize - countingSource.count
                    source.read(remainingBytes)

                    Out.writeWarning(
                        "Skipping custom section '$name' that consists of " +
                                "$remainingBytes bytes"
                    )
                }
                0x1u -> builder.types += context.deserializeVector(source)
                0x2u -> builder.imports += context.deserializeVector(source)
                0x3u -> builder.functions += source.readVarUInt32Vector()
                0x4u -> builder.tables += context.deserializeVector(source)
                0x5u -> builder.memories += context.deserializeVector(source)
                0x6u -> builder.globals += context.deserializeVector(source)
                0x7u -> builder.exports += context.deserializeVector(source)
                0x8u -> builder.startFunctionIndex = source.readVarUInt32()
                0x9u -> builder.elements += context.deserializeVector(source)
                0xAu -> builder.codes += context.deserializeVector(source)
                0xBu -> builder.datas += context.deserializeVector(source)

                else -> throw SerializationException(
                    "Unknown section type: $sectionType"
                )
            }
        }

        return Module(
            builder.codes,
            builder.datas,
            builder.elements,
            builder.exports,
            builder.functions,
            builder.globals,
            builder.imports,
            builder.memories,
            builder.startFunctionIndex,
            builder.tables,
            builder.types
        )
    }

    override fun serialize(
        instance: Module,
        sink: ByteSink,
        context: Context
    ) {
        sink.write(
            0x00, 0x61, 0x73, 0x6D, // File Magic
            0x01, 0x00, 0x00, 0x00  // Version
        )

        serializeSection(sink, 0x01u) { sectionSink ->
            context.serializeVector(sectionSink, instance.types)
        }

        serializeSection(sink, 0x02u) { sectionSink ->
            context.serializeVector(sectionSink, instance.imports)
        }

        serializeSection(sink, 0x03u) { sectionSink ->
            sectionSink.writeVarUInt32Vector(instance.functions)
        }

        serializeSection(sink, 0x04u) { sectionSink ->
            context.serializeVector(sectionSink, instance.tables)
        }

        serializeSection(sink, 0x05u) { sectionSink ->
            context.serializeVector(sectionSink, instance.memories)
        }

        serializeSection(sink, 0x06u) { sectionSink ->
            context.serializeVector(sectionSink, instance.globals)
        }

        serializeSection(sink, 0x07u) { sectionSink ->
            context.serializeVector(sectionSink, instance.exports)
        }

        instance.startFunctionIndex?.let { index ->
            serializeSection(sink, 0x08u) { sectionSink ->
                sectionSink.writeVarUInt32(index)
            }
        }

        serializeSection(sink, 0x09u) { sectionSink ->
            context.serializeVector(sectionSink, instance.elements)
        }

        serializeSection(sink, 0x0Au) { sectionSink ->
            context.serializeVector(sectionSink, instance.codes)
        }

        serializeSection(sink, 0x0Bu) { sectionSink ->
            context.serializeVector(sectionSink, instance.datas)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private inline fun serializeSection(
        sink: ByteSink,
        sectionId: UByte,
        bodyBuilder: (ByteSink) -> Unit
    ) {
        val body = withMemoryByteSink(bodyBuilder)
        sink.write(sectionId)
        sink.writeInt8Vector(body)
    }
}

private data class ModuleBuilder(
    val codes: MutableList<Code> = mutableListOf(),
    val datas: MutableList<Data> = mutableListOf(),
    val elements: MutableList<Element> = mutableListOf(),
    val exports: MutableCollection<Export> = mutableListOf(),
    val functions: MutableList<UInt> = mutableListOf(),
    val globals: MutableList<Global> = mutableListOf(),
    val imports: MutableList<Import> = mutableListOf(),
    val memories: MutableList<Limits> = mutableListOf(),
    var startFunctionIndex: UInt? = null,
    val tables: MutableList<TableType> = mutableListOf(),
    val types: MutableList<FunctionType> = mutableListOf()
)
