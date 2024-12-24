package dev.fir3.wedding.output.model

import dev.fir3.iwan.io.wasm.models.*

data class MutableOutputContainer(
    override val types: MutableList<FunctionType> = mutableListOf(),
    override val functions: MutableList<UInt> = mutableListOf(),
    override val tables: MutableList<TableType> = mutableListOf(),
    override val memories: MutableList<MemoryType> = mutableListOf(),
    override val globals: MutableList<Global> = mutableListOf(),
    override val elements: MutableList<Element> = mutableListOf(),
    override val data: MutableList<Data> = mutableListOf(),
    override val imports: MutableList<Import> = mutableListOf(),
    override val exports: MutableList<Export> = mutableListOf(),
    override val codes: MutableList<Code> = mutableListOf(),
    override var startFunction: UInt? = null
) : OutputContainer
