package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.deserialize
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.expect
import dev.fir3.iwan.io.wasm.models.Function
import dev.fir3.iwan.io.wasm.models.Module
import dev.fir3.iwan.io.wasm.models.sections.*
import java.io.IOException

internal object ModuleStrategy : DeserializationStrategy<Module> {
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

        val functions = codes.mapIndexed { index, code ->
            val typeIndex = functionTypes[index]
            val locals = code.locals.flatMap { (type, count) ->
                count.downTo(1u).map { type }
            }

            Function(typeIndex, locals, code.body)
        }

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

        return Module(
            types,
            functions,
            tables,
            memories,
            globals,
            elements,
            data,
            imports,
            exports
        )
    }
}
