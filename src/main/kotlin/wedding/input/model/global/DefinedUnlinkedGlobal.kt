package dev.fir3.wedding.input.model.global

import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.GlobalType

internal data class DefinedUnlinkedGlobal(
    override val exportName: String?,
    override val index: UInt,
    val initializer: Expression,
    override val module: String,
    override val type: GlobalType
) : UnlinkedGlobal
