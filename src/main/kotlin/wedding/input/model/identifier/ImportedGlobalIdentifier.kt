package dev.fir3.wedding.input.model.identifier

import dev.fir3.iwan.io.wasm.models.GlobalType

internal data class ImportedGlobalIdentifier(
    val global: String,
    val module: String,
    val sourceModule: String,
    override val type: GlobalType
) : GlobalIdentifier
