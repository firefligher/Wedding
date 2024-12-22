package dev.fir3.iwan.io.wasm.models.sections

import dev.fir3.iwan.io.wasm.models.Global

data class GlobalSection(val globals: List<Global>): Section
