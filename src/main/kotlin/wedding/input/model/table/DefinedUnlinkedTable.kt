package dev.fir3.wedding.input.model.table

import dev.fir3.iwan.io.wasm.models.TableType

internal data class DefinedUnlinkedTable(
    override val exportName: String?,
    override val index: UInt,
    override val module: String,
    override val type: TableType
) : UnlinkedTable
