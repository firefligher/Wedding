package dev.fir3.iwan.io.wasm.models.sections

import dev.fir3.iwan.io.wasm.models.TableType

data class TableSection(val types: List<TableType>): Section
