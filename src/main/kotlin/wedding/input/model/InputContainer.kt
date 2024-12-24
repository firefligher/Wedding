package dev.fir3.wedding.input.model

import dev.fir3.wedding.input.model.data.UnlinkedData
import dev.fir3.wedding.input.model.function.UnlinkedFunction
import dev.fir3.wedding.input.model.global.UnlinkedGlobal
import dev.fir3.wedding.input.model.memory.UnlinkedMemory

internal interface InputContainer {
    val datas: Set<UnlinkedData>
    val functions: Set<UnlinkedFunction>
    val globals: Set<UnlinkedGlobal>
    val memories: Set<UnlinkedMemory>
}
