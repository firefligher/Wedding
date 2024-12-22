package dev.fir3.wedding.input.data

import dev.fir3.wedding.IndexedObject

internal sealed interface DataEntry : IndexedObject {
    val initializers: List<Byte>
}
