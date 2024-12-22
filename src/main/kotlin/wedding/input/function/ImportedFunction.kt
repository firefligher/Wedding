package dev.fir3.wedding.input.function

import dev.fir3.iwan.io.wasm.models.FunctionType

internal data class ImportedFunction(
    val functionName: String,
    override val index: UInt,
    override val isStart: Boolean,
    val sourceModule: String,
    override val moduleName: String,
    override val type: FunctionType
) : FunctionEntry
