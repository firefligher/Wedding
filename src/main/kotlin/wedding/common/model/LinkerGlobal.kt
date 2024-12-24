package dev.fir3.wedding.common.model

import dev.fir3.iwan.io.wasm.models.GlobalType

internal interface LinkerGlobal : Exportable {
    val type: GlobalType
}
