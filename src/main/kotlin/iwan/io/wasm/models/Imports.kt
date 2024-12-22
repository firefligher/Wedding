package dev.fir3.iwan.io.wasm.models

sealed interface Import {
    val module: String
    val name: String
}

data class FunctionImport(
    override val module: String,
    override val name: String,
    val typeIndex: UInt
): Import

data class GlobalImport(
    override val module: String,
    override val name: String,
    val type: GlobalType
): Import

data class MemoryImport(
    override val module: String,
    override val name: String,
    val type: MemoryType
): Import

data class TableImport(
    override val module: String,
    override val name: String,
    val type: TableType
): Import
