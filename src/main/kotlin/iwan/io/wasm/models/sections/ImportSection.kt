package dev.fir3.iwan.io.wasm.models.sections

import dev.fir3.iwan.io.wasm.models.Import

data class ImportSection(val imports: List<Import>): Section
