package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.foundation.IOException
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.SerializationException
import dev.fir3.wedding.io.serialization.deserialize

@Throws(SerializationException::class)
inline fun <reified TElement : Any> Context.deserializeVector(
    source: ByteSource
): List<TElement> {
    val count = try {
        source.readVarUInt32()
    } catch (exception: IOException) {
        throw SerializationException(exception)
    }

    return List(count.toInt()) { _ ->
        deserialize(source)
    }
}

@Throws(SerializationException::class)
fun <TElement : Any> Context.serializeVector(
    sink: ByteSink,
    vector: Collection<TElement>
) {
    try {
        sink.writeVarUInt32(vector.size.toUInt())
    } catch (exception: IOException) {
        throw SerializationException(exception)
    }

    vector.forEach { element ->
        serialize(sink, element)
    }
}
