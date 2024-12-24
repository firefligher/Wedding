package dev.fir3.wedding.input.model.data

import dev.fir3.iwan.io.wasm.models.Expression

data class ActiveUnlinkedData(
    override val index: UInt,
    override val initializers: List<Byte>,
    val memoryIndex: UInt,
    override val module: String,
    val offset: Expression
) : UnlinkedData
