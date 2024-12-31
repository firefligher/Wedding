package dev.fir3.wedding.linking.model

import dev.fir3.wedding.linking.model.data.RelocatedData
import dev.fir3.wedding.linking.model.function.RelocatedFunction
import dev.fir3.wedding.linking.model.global.RelocatedGlobal
import dev.fir3.wedding.linking.model.memory.RelocatedMemory
import dev.fir3.wedding.linking.model.table.RelocatedTable

internal data class MutableRelocationContainer(
    override val datas: MutableSet<RelocatedData> = mutableSetOf(),
    override val dataRelocations: MutableRelocationTable =
        MutableRelocationTable(),

    override val functions: MutableSet<RelocatedFunction> = mutableSetOf(),
    override val functionRelocations: MutableRelocationTable =
        MutableRelocationTable(),

    override val globals: MutableSet<RelocatedGlobal> = mutableSetOf(),
    override val globalRelocations: MutableRelocationTable =
        MutableRelocationTable(),

    override val memories: MutableSet<RelocatedMemory> = mutableSetOf(),
    override val memoryRelocations: MutableRelocationTable =
        MutableRelocationTable(),

    override val tables: MutableSet<RelocatedTable> = mutableSetOf(),
    override val tableRelocations: MutableRelocationTable =
        MutableRelocationTable()
) : RelocationContainer
