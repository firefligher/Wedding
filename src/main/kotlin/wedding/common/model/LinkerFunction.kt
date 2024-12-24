package dev.fir3.wedding.common.model

import dev.fir3.iwan.io.wasm.models.FunctionType

internal interface LinkerFunction : Exportable {
    /**
     * If this function is the start function of the corresponding module.
     */
    val isStart: Boolean

    /**
     * The type of this function.
     */
    val type: FunctionType
}
