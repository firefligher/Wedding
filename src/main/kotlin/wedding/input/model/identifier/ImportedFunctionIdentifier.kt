package dev.fir3.wedding.input.model.identifier

import dev.fir3.iwan.io.wasm.models.FunctionType

internal data class ImportedFunctionIdentifier(
    val function: String,
    val module: String,
    val sourceModule: String,
    val type: FunctionType
) : Identifier
