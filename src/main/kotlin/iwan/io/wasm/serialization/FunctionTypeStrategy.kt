package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.deserialize
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.expectInt8
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.FunctionType
import dev.fir3.iwan.io.wasm.models.valueTypes.ValueType
import java.io.IOException

internal object FunctionTypeStrategy : DeserializationStrategy<FunctionType> {
    private const val PREFIX: Byte = 0x60

    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): FunctionType {
        source.expectInt8(PREFIX)

        // Parameter types

        val parameterCount = source.readVarUInt32()
        val parameterTypes = mutableListOf<ValueType>()

        for (i in 0u until parameterCount) {
            parameterTypes.add(context.deserialize(source))
        }

        // Result types

        val resultCount = source.readVarUInt32()
        val resultTypes = mutableListOf<ValueType>()

        for (i in 0u until resultCount) {
            resultTypes.add(context.deserialize(source))
        }

        return FunctionType(parameterTypes, resultTypes)
    }
}
