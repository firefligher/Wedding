package dev.fir3.wedding.linking.model.table

import dev.fir3.iwan.io.wasm.models.TableType

internal data class ImportedRelocatedTable(
    override val exportName: String?,
    override val index: UInt,
    override val module: String,
    override val originalModule: String,
    val sourceModule: String,
    val tableName: String,
    override val type: TableType
) : RelocatedTable
