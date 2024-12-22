package dev.fir3.iwan.io.wasm.models.sections

data class StartSection(
    val functionIndex: UInt
) : Section
