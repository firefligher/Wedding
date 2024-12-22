package dev.fir3.wedding.linker.function

import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.FunctionType
import dev.fir3.iwan.io.wasm.models.valueTypes.ValueType

data class LinkedExportedFunction(
    val expression: Expression,
    val functionName: String,
    override val index: UInt,
    override val isStart: Boolean,
    val locals: List<Pair<ValueType, UInt>>,
    override val moduleName: String,
    override val originalModule: String,
    override val type: FunctionType
) : LinkedFunctionEntry
