package dev.fir3.wedding.wasm

sealed interface Element {
    val initializers: List<List<Instruction>>
    val type: ReferenceType
}

data class ActiveElement(
    override val initializers: List<List<Instruction>>,
    val offset: List<Instruction>,
    val tableIndex: UInt,
    override val type: ReferenceType
): Element

data class DeclarativeElement(
    override val initializers: List<List<Instruction>>,
    override val type: ReferenceType
): Element

data class PassiveElement(
    override val initializers: List<List<Instruction>>,
    override val type: ReferenceType
): Element
