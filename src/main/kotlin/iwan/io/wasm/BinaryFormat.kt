package dev.fir3.iwan.io.wasm

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationContextBuilder
import dev.fir3.iwan.io.serialization.deserialize
import dev.fir3.iwan.io.serialization.register
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.wasm.models.Module
import dev.fir3.iwan.io.wasm.serialization.*
import dev.fir3.iwan.io.wasm.serialization.instructions.*
import dev.fir3.iwan.io.wasm.serialization.instructions.InstructionStrategy
import dev.fir3.iwan.io.wasm.serialization.instructions.BlockTypeStrategy
import dev.fir3.iwan.io.wasm.serialization.valueTypes.NumberTypeStrategy
import dev.fir3.iwan.io.wasm.serialization.valueTypes.ReferenceTypeStrategy
import dev.fir3.iwan.io.wasm.serialization.valueTypes.ValueTypeStrategy
import dev.fir3.iwan.io.wasm.serialization.valueTypes.VectorTypeStrategy
import java.io.IOException

object BinaryFormat {
    private val _context: DeserializationContext

    init {
        val b = DeserializationContextBuilder()

        b.register(BlockTypeStrategy)
        b.register(CodeStrategy)
        b.register(DataStrategy)
        b.register(ElementStrategy)
        b.register(ExportStrategy)
        b.register(ExpressionStrategy)
        b.register(FunctionTypeStrategy)
        b.register(GlobalStrategy)
        b.register(GlobalTypeStrategy)
        b.register(ImportStrategy)
        b.register(InstructionStrategy)
        b.register(MemoryTypeStrategy)
        b.register(ModuleStrategy)
        b.register(NumberTypeStrategy)
        b.register(ReferenceTypeStrategy)
        b.register(SectionStrategy)
        b.register(TableTypeStrategy)
        b.register(ValueTypeStrategy)
        b.register(VectorTypeStrategy)

        _context = b.build()
    }

    @Throws(IOException::class)
    fun deserializeModule(source: ByteSource) = _context
        .deserialize<Module>(source)
}
