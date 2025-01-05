package dev.fir3.wedding.io.serialization

import dev.fir3.wedding.io.foundation.ByteSource

@Throws(SerializationException::class)
inline fun <reified TModel: Any> Context.deserialize(
    source: ByteSource
) = deserialize(source, TModel::class)
