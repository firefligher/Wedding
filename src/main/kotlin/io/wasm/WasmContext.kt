package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.serialization.AbstractContext
import dev.fir3.wedding.io.serialization.SerializationException
import dev.fir3.wedding.wasm.Module

object WasmContext : AbstractContext() {
    init {
        register(BlockTypeStrategy)
        register(CodeStrategy)
        register(DataStrategy)
        register(ElementStrategy)
        register(ExportStrategy)
        register(FunctionTypeStrategy)
        register(GlobalStrategy)
        register(ImportStrategy)
        register(InstructionStrategy)
        register(LimitsStrategy)
        register(ModuleStrategy)
        register(TableTypeStrategy)
        register(ValueTypeStrategy)
    }

    @Throws(SerializationException::class)
    fun deserialize(source: ByteSource) = deserialize(source, Module::class)
}
