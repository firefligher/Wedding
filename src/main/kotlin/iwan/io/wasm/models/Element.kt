package dev.fir3.iwan.io.wasm.models

import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType

sealed interface Element {
    val type: ReferenceType
    val initializers: List<Expression>
}

data class ActiveElement(
    override val type: ReferenceType,
    override val initializers: List<Expression>,
    val table: UInt, val offset: Expression
): Element

data class DeclarativeElement(
    override val type: ReferenceType,
    override val initializers: List<Expression>
): Element

data class PassiveElement(
    override val type: ReferenceType,
    override val initializers: List<Expression>
): Element
