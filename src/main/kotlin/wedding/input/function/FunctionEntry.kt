package dev.fir3.wedding.input.function

import dev.fir3.iwan.io.wasm.models.FunctionType
import dev.fir3.wedding.input.IndexedObject

internal sealed interface FunctionEntry : IndexedObject {
    val isStart: Boolean
    val type: FunctionType
}
