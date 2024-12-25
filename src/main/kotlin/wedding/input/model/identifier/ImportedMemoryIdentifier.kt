package dev.fir3.wedding.input.model.identifier

import dev.fir3.iwan.io.wasm.models.MemoryType

internal data class ImportedMemoryIdentifier(
    val memory: String,
    val module: String,
    val sourceModule: String,
    override val type: MemoryType
) : MemoryIdentifier
