package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.foundation.readInt8
import dev.fir3.wedding.io.foundation.write
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.io.serialization.deserialize
import dev.fir3.wedding.wasm.Global
import dev.fir3.wedding.wasm.Instruction
import dev.fir3.wedding.wasm.ValueType

object GlobalStrategy : Strategy<Global> {
    override fun deserialize(source: ByteSource, context: Context): Global {
        val type = context.deserialize<ValueType>(source)
        val isMutable = source.readInt8() == 1.toByte()
        val initializer = context.deserializeVector<Instruction>(source)

        return Global(
            initializer = initializer,
            isMutable = isMutable,
            type = type
        )
    }

    override fun serialize(
        instance: Global,
        sink: ByteSink,
        context: Context
    ) {
        context.serialize(sink, instance.type)
        sink.write(
            if (instance.isMutable) 1
            else 0
        )

        context.serializeVector(sink, instance.initializer)
    }
}
