package dev.fir3.wedding.wasm

sealed interface Import {
    val module: String
    val name: String
}

data class FunctionImport(
    override val module: String,
    override val name: String,
    val typeIndex: UInt
) : Import

data class TableImport(
    override val module: String,
    override val name: String,
    val tableType: TableType
) : Import
