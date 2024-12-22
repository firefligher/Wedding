package dev.fir3.wedding.input.global

import dev.fir3.iwan.io.wasm.models.GlobalType

internal data class ImportedGlobal(
    val globalName: String,
    override val index: UInt,
    override val moduleName: String,
    val sourceModule: String,
    override val type: GlobalType
) : GlobalEntry
