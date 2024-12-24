package dev.fir3.wedding.linking.model.global

import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.GlobalType

internal data class DefinedRelocatedGlobal(
    override val exportName: String?,
    override val index: UInt,
    val initializer: Expression,
    override val module: String,
    override val originalModule: String,
    override val type: GlobalType
) : RelocatedGlobal
