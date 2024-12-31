package dev.fir3.wedding.input.model

import dev.fir3.wedding.input.model.data.UnlinkedData
import dev.fir3.wedding.input.model.element.UnlinkedElement
import dev.fir3.wedding.input.model.function.UnlinkedFunction
import dev.fir3.wedding.input.model.global.UnlinkedGlobal
import dev.fir3.wedding.input.model.memory.UnlinkedMemory
import dev.fir3.wedding.input.model.table.UnlinkedTable

internal interface InputContainer {
    val datas: Set<UnlinkedData>
    val elements: Set<UnlinkedElement>
    val functions: Set<UnlinkedFunction>
    val globals: Set<UnlinkedGlobal>
    val memories: Set<UnlinkedMemory>
    val tables: Set<UnlinkedTable>
}
