package dev.fir3.wedding.input.global

import dev.fir3.iwan.io.wasm.models.GlobalType
import dev.fir3.wedding.input.IndexedObject

internal sealed interface GlobalEntry : IndexedObject {
    val type: GlobalType
}
