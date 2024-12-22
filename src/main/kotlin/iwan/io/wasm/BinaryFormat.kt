package dev.fir3.iwan.io.wasm

import dev.fir3.iwan.io.serialization.*
import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationContextBuilder
import dev.fir3.iwan.io.serialization.register
import dev.fir3.iwan.io.sink.ByteSink
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

internal object BinaryFormat {
    private val deserializationContext: DeserializationContext
    private val serializationContext: SerializationContext

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

        deserializationContext = b.build()
    }

    init {
        val b = SerializationContextBuilder()

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

        serializationContext = b.build()
    }

    @Throws(IOException::class)
    fun deserializeModule(source: ByteSource) = deserializationContext
        .deserialize<Module>(source)

    @Throws(IOException::class)
    fun serializeModule(sink: ByteSink, module: Module) =
        serializationContext.serialize(sink, module)
}
