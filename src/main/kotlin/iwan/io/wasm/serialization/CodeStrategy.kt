package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.deserialize
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.Code
import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.valueTypes.ValueType
import java.io.IOException

internal object CodeStrategy : DeserializationStrategy<Code> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): Code {
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

        val body = context.deserialize<Expression>(source)
        return Code(locals, body)
    }
}