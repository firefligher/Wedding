package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.foundation.withMemoryByteSink
import dev.fir3.wedding.io.foundation.write
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.io.serialization.deserialize
import dev.fir3.wedding.wasm.Code
import dev.fir3.wedding.wasm.ValueType

object CodeStrategy : Strategy<Code> {
    override fun deserialize(source: ByteSource, context: Context): Code {
        // NOTE:    We ignore the initial size for now.

        source.readVarUInt32()

        // Read locals

        val localsCount = source.readVarUInt32()
        val locals = mutableListOf<Pair<ValueType, UInt>>()

        for (i in 0u until localsCount) {
            val count = source.readVarUInt32()
            val type = context.deserialize<ValueType>(source)

            locals += Pair(type, count)
        }

        // Read body

        val body = context.deserializeInstructions(source)
        return Code(body, locals)
    }

    override fun serialize(instance: Code, sink: ByteSink, context: Context) {
        val bodyBytes = withMemoryByteSink { memorySink ->
            memorySink.writeVarUInt32(instance.locals.size.toUInt())

            instance.locals.forEach { (type, count) ->
                memorySink.writeVarUInt32(count)
                context.serialize(memorySink, type)
            }

            context.serializeInstructions(memorySink, instance.body)
        }

        sink.writeVarUInt32(bodyBytes.size.toUInt())
        sink.write(*bodyBytes)
    }
}
