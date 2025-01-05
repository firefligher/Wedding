package dev.fir3.wedding.wasm

sealed interface Export {
    val name: String
}

data class FunctionExport(
    val functionIndex: UInt,
    override val name: String
) : Export

data class GlobalExport(
    val globalIndex: UInt,
    override val name: String
) : Export

data class MemoryExport(
    val memoryIndex: UInt,
    override val name: String
) : Export

data class TableExport(
    override val name: String,
    val tableIndex: UInt
) : Export
