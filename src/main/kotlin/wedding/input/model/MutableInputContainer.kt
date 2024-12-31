package dev.fir3.wedding.input.model

import dev.fir3.wedding.input.model.data.UnlinkedData
import dev.fir3.wedding.input.model.element.UnlinkedElement
import dev.fir3.wedding.input.model.function.UnlinkedFunction
import dev.fir3.wedding.input.model.global.UnlinkedGlobal
import dev.fir3.wedding.input.model.memory.UnlinkedMemory
import dev.fir3.wedding.input.model.table.UnlinkedTable

internal data class MutableInputContainer(
    override val datas: MutableSet<UnlinkedData> = mutableSetOf(),
    override val elements: MutableSet<UnlinkedElement> = mutableSetOf(),
    override val functions: MutableSet<UnlinkedFunction> = mutableSetOf(),
    override val globals: MutableSet<UnlinkedGlobal> = mutableSetOf(),
    override val memories: MutableSet<UnlinkedMemory> = mutableSetOf(),
    override val tables: MutableSet<UnlinkedTable> = mutableSetOf()
) : InputContainer
