package dev.fir3.iwan.io.wasm.models

sealed interface Export {
    val name: String
}

data class FunctionExport(
    override val name: String,
    val functionIndex: UInt
) : Export

data class GlobalExport(
    override val name: String,
    val globalIndex: UInt
) : Export

data class MemoryExport(
    override val name: String,
    val memoryIndex: UInt
) : Export

data class TableExport(
    override val name: String,
    val tableIndex: UInt
) : Export
