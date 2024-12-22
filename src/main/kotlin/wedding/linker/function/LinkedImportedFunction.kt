package dev.fir3.wedding.linker.function

import dev.fir3.iwan.io.wasm.models.FunctionType

internal data class LinkedImportedFunction(
    val functionName: String,
    override val index: UInt,
    override val isStart: Boolean,
    override val moduleName: String,
    override val originalModule: String,
    val sourceModule: String,
    override val type: FunctionType
) : LinkedFunctionEntry