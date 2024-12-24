package dev.fir3.wedding.linking.model.function

import dev.fir3.iwan.io.wasm.models.FunctionType

internal data class ImportedRelocatedFunction(
    override val exportName: String?,
    val functionName: String,
    override val isStart: Boolean,
    override val type: FunctionType,
    override val index: UInt,
    override val module: String,
    override val originalModule: String,
    val sourceModule: String
) : RelocatedFunction
