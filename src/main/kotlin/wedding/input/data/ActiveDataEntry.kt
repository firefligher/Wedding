package dev.fir3.wedding.input.data

import dev.fir3.iwan.io.wasm.models.Expression

data class ActiveDataEntry(
    override val index: UInt,
    override val initializers: List<Byte>,
    val memoryIndex: UInt,
    override val moduleName: String,
    val offset: Expression
) : DataEntry
