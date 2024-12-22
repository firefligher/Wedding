package dev.fir3.iwan.io.serialization

import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readVarUInt32

internal inline fun <reified TValue : Any> DeserializationContext.deserialize(
    source: ByteSource
) = deserialize(source, TValue::class)

internal inline fun <
        reified TValue : Any
> DeserializationContext.deserializeVector(
    source: ByteSource
) : List<TValue> {
    val count = source.readVarUInt32()
    val list = mutableListOf<TValue>()

    for (i in 0u until count) {
        list.add(deserialize(source))
    }

    return list
}
