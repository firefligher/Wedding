package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.deserialize
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readInt8
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.*
import java.io.IOException

internal object DataStrategy : DeserializationStrategy<Data> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ) = when (val typeId = source.readInt8()) {
        0.toByte() -> {
            val e = context.deserialize<Expression>(source)
            val bCount = source.readVarUInt32()
            val b = mutableListOf<Byte>()

            for (i in 0u until bCount) {
                b.add(source.readInt8())
            }

            ActiveData(b, 0u, e)
        }

        1.toByte() -> {
            val bCount = source.readVarUInt32()
            val b = mutableListOf<Byte>()

            for (i in 0u until bCount) {
                b.add(source.readInt8())
            }

            PassiveData(b)
        }

        2.toByte() -> {
            val x = source.readVarUInt32()
            val e = context.deserialize<Expression>(source)
            val bCount = source.readVarUInt32()
            val b = mutableListOf<Byte>()

            for (i in 0u until bCount) {
                b.add(source.readInt8())
            }

            ActiveData(b, x, e)
        }

        else -> throw IOException("Invalid data type '$typeId'")
    }
}
