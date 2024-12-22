package dev.fir3.wedding.input.global

import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.GlobalType

internal data class ExportedGlobal(
    val globalName: String,
    override val index: UInt,
    val initializer: Expression,
    override val moduleName: String,
    override val type: GlobalType
) : GlobalEntry
