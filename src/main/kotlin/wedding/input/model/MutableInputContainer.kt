package dev.fir3.wedding.input.model

import dev.fir3.wedding.input.model.data.UnlinkedData
import dev.fir3.wedding.input.model.function.UnlinkedFunction
import dev.fir3.wedding.input.model.global.UnlinkedGlobal
import dev.fir3.wedding.input.model.memory.UnlinkedMemory

internal data class MutableInputContainer(
    override val datas: MutableSet<UnlinkedData> = mutableSetOf(),
    override val functions: MutableSet<UnlinkedFunction> = mutableSetOf(),
    override val globals: MutableSet<UnlinkedGlobal> = mutableSetOf(),
    override val memories: MutableSet<UnlinkedMemory> = mutableSetOf()
) : InputContainer
