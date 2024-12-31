package dev.fir3.wedding.input.model.element

import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType

internal data class ActiveUnlinkedElement(
    override val index: UInt,
    override val initializers: List<Expression>,
    override val module: String,
    val offset: Expression,
    val tableIndex: UInt,
    override val type: ReferenceType
) : UnlinkedElement
