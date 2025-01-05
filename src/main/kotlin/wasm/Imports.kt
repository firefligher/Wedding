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

data class GlobalImport(
    val isMutable: Boolean,
    override val module: String,
    override val name: String,
    val type: ValueType
) : Import

data class MemoryImport(
    val limits: Limits,
    override val module: String,
    override val name: String
): Import

data class TableImport(
    override val module: String,
    override val name: String,
    val tableType: TableType
) : Import
