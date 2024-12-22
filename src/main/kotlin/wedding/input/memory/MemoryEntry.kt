package dev.fir3.wedding.input.memory

import dev.fir3.iwan.io.wasm.models.MemoryType
import dev.fir3.wedding.IndexedObject

internal sealed interface MemoryEntry : IndexedObject {
    val type: MemoryType
}
