package dev.fir3.wedding.linker.function

import dev.fir3.iwan.io.wasm.models.FunctionType
import dev.fir3.wedding.IndexedObject

internal sealed interface LinkedFunctionEntry : IndexedObject {
    val isStart: Boolean
    val originalModule: String
    val type: FunctionType
}
