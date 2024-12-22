package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.*
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.write
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.expect
import dev.fir3.iwan.io.wasm.models.Module
import dev.fir3.iwan.io.wasm.models.sections.*
import java.io.IOException

internal object ModuleStrategy :
    DeserializationStrategy<Module>,
    SerializationStrategy<Module> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): Module {
        source.expect(
            0x00, 0x61, 0x73, 0x6D, // File Magic
            0x01, 0x00, 0x00, 0x00  // Version
        )

        val sections = mutableListOf<Section>()

        while (!source.isEof()) {
            sections.add(context.deserialize(source))
        }

        // TODO: Verify the section order.
        // TDDO: Support multiple sections of same type (as specified)

        val types = sections
            .filterIsInstance<TypeSection>()
            .singleOrNull()?.types ?: emptyList()

        val functionTypes = sections
            .filterIsInstance<FunctionSection>()
            .singleOrNull()
            ?.types ?: emptyList()

        val codes = sections
            .filterIsInstance<CodeSection>()
            .singleOrNull()?.codes ?: emptyList()

        val tables = sections
            .filterIsInstance<TableSection>()
            .singleOrNull()
            ?.types ?: emptyList()

        val memories = sections
            .filterIsInstance<MemorySection>()
            .singleOrNull()
            ?.memories ?: emptyList()

        val globals = sections
            .filterIsInstance<GlobalSection>()
            .singleOrNull()
            ?.globals ?: emptyList()

        val elements = sections
            .filterIsInstance<ElementSection>()
            .singleOrNull()
            ?.elements ?: emptyList()

        val data = sections
            .filterIsInstance<DataSection>()
            .singleOrNull()
            ?.data ?: emptyList()

        val imports = sections
            .filterIsInstance<ImportSection>()
            .singleOrNull()
            ?.imports ?: emptyList()

        val exports = sections
            .filterIsInstance<ExportSection>()
            .singleOrNull()
            ?.exports ?: emptyList()

        val startFunction = sections
            .filterIsInstance<StartSection>()
            .singleOrNull()
            ?.function

        return Module(
            types,
            functionTypes,
            tables,
            memories,
            globals,
            elements,
            data,
            imports,
            exports,
            codes,
            startFunction
        )
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: Module
    ) {
        sink.write(
            0x00, 0x61, 0x73, 0x6D, // File Magic
            0x01, 0x00, 0x00, 0x00  // Version
        )

        context.serialize(sink, TypeSection(value.types))
        context.serialize(sink, ImportSection(value.imports))
        context.serialize(sink, FunctionSection(value.functions))

        if (value.tables.isNotEmpty())
            context.serialize(sink, TableSection(value.tables))

        if (value.memories.isNotEmpty())
            context.serialize(sink, MemorySection(value.memories))

        context.serialize(sink, GlobalSection(value.globals))
        context.serialize(sink, ExportSection(value.exports))

        value.startFunction?.let { function ->
            context.serialize(sink, StartSection(function))
        }

        if (value.elements.isNotEmpty())
            context.serialize(sink, ElementSection(value.elements))

        context.serialize(sink, CodeSection(value.codes))
        context.serialize(sink, DataSection(value.data))
    }
}
