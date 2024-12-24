package dev.fir3.wedding.input.model.identifier

import dev.fir3.iwan.io.wasm.models.MemoryType

internal data class ExportedMemoryIdentifier(
    val memory: String,
    val module: String,
    val type: MemoryType
) : Identifier
