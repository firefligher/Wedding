package dev.fir3.wedding.linking.model.element

import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType

internal data class ActiveRelocatedElement(
    override val index: UInt,
    override val initializers: List<Expression>,
    override val module: String,
    val offset: Expression,
    override val originalModule: String,
    val tableIndex: UInt,
    override val type: ReferenceType
) : RelocatedElement
