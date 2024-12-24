package dev.fir3.wedding.linking.model.data

import dev.fir3.iwan.io.wasm.models.Expression

internal data class ActiveRelocatedData(
    override val index: UInt,
    override val initializers: List<Byte>,
    val memoryIndex: UInt,
    override val module: String,
    val offset: Expression,
    override val originalModule: String
) : RelocatedData
