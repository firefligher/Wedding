package dev.fir3.iwan.io.wasm.models.sections

import dev.fir3.iwan.io.wasm.models.MemoryType

data class MemorySection(
    val memories: List<MemoryType>
): Section
