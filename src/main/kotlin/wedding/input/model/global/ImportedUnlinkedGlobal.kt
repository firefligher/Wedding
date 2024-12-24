package dev.fir3.wedding.input.model.global

import dev.fir3.iwan.io.wasm.models.GlobalType

internal data class ImportedUnlinkedGlobal(
    override val exportName: String?,
    val globalName: String,
    override val index: UInt,
    override val module: String,
    val sourceModule: String,
    override val type: GlobalType
) : UnlinkedGlobal
