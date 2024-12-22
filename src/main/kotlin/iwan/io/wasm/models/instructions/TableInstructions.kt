package dev.fir3.iwan.io.wasm.models.instructions

interface TableInstruction : Instruction

data class ElementDropInstruction(val elementIndex: UInt): TableInstruction

data class TableCopyInstruction(
    val tableIndex1: UInt,
    val tableIndex2: UInt
): TableInstruction


data class TableFillInstruction(val tableIndex: UInt): TableInstruction

data class TableGetInstruction(val tableIndex: UInt): TableInstruction

data class TableGrowInstruction(val tableIndex: UInt): TableInstruction

data class TableInitInstruction(
    val tableIndex: UInt,
    val elementIndex: UInt
): TableInstruction

data class TableSetInstruction(val tableIndex: UInt): TableInstruction

data class TableSizeInstruction(val tableIndex: UInt): TableInstruction
