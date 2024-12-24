package dev.fir3.wedding.input.model.identifier

import dev.fir3.iwan.io.wasm.models.FunctionType

internal data class ExportedFunctionIdentifier(
    val function: String,
    val module: String,
    val type: FunctionType
) : Identifier
