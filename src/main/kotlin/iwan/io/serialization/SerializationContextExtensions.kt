package dev.fir3.iwan.io.serialization

import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.writeVarUInt32

internal fun <TElement : Any> SerializationContext.serializeVector(
    sink: ByteSink,
    vector: List<TElement>
) {
    sink.writeVarUInt32(vector.size.toUInt())
    vector.forEach { serialize(sink, it) }
}
