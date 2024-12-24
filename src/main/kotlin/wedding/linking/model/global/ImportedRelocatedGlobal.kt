package dev.fir3.wedding.linking.model.global

import dev.fir3.iwan.io.wasm.models.GlobalType

data class ImportedRelocatedGlobal(
    override val exportName: String?,
    val globalName: String,
    override val index: UInt,
    override val module: String,
    override val originalModule: String,
    val sourceModule: String,
    override val type: GlobalType
) : RelocatedGlobal
