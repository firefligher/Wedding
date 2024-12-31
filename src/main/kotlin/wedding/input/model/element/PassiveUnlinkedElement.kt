package dev.fir3.wedding.input.model.element

import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType

internal data class PassiveUnlinkedElement(
    override val index: UInt,
    override val initializers: List<Expression>,
    override val module: String,
    override val type: ReferenceType
) : UnlinkedElement
