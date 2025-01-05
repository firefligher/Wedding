package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.foundation.write
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.wasm.FunctionType
import dev.fir3.wedding.wasm.ValueType

object FunctionTypeStrategy : Strategy<FunctionType> {
    override fun deserialize(
        source: ByteSource,
        context: Context
    ): FunctionType {
        source.expect(0x60)

        val parameterTypes = context.deserializeVector<ValueType>(source)
        val resultTypes = context.deserializeVector<ValueType>(source)

        return FunctionType(parameterTypes, resultTypes)
    }

    override fun serialize(
        instance: FunctionType,
        sink: ByteSink,
        context: Context
    ) {
        sink.write(0x60)
        context.serializeVector(sink, instance.parameterTypes)
        context.serializeVector(sink, instance.resultTypes)
    }
}
