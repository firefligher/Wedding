package dev.fir3.wedding.input.model.function

import dev.fir3.iwan.io.wasm.models.FunctionType

internal data class ImportedUnlinkedFunction(
    override val exportName: String?,
    val functionName: String,
    override val index: UInt,
    override val isStart: Boolean,
    override val module: String,
    val sourceModule: String,
    override val type: FunctionType
) : UnlinkedFunction
