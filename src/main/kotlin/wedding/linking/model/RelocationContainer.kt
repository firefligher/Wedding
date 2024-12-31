package dev.fir3.wedding.linking.model

import dev.fir3.wedding.linking.model.data.RelocatedData
import dev.fir3.wedding.linking.model.function.RelocatedFunction
import dev.fir3.wedding.linking.model.global.RelocatedGlobal
import dev.fir3.wedding.linking.model.memory.RelocatedMemory
import dev.fir3.wedding.linking.model.table.RelocatedTable

internal interface RelocationContainer : RelocationTablesContainer {
    val datas: Set<RelocatedData>
    val functions: Set<RelocatedFunction>
    val globals: Set<RelocatedGlobal>
    val memories: Set<RelocatedMemory>
    val tables: Set<RelocatedTable>
}
