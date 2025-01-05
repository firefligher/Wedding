package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.io.serialization.deserialize
import dev.fir3.wedding.wasm.Limits
import dev.fir3.wedding.wasm.ReferenceType
import dev.fir3.wedding.wasm.TableType

object TableTypeStrategy : Strategy<TableType> {
    override fun deserialize(source: ByteSource, context: Context): TableType {
        val elementType = context.deserialize<ReferenceType>(source)
        val limits = context.deserialize<Limits>(source)

        return TableType(elementType, limits)
    }

    override fun serialize(
        instance: TableType,
        sink: ByteSink,
        context: Context
    ) {
        context.serialize(sink, instance.elementType)
        context.serialize(sink, instance.limits)
    }
}
