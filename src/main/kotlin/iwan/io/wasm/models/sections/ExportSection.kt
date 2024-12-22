package dev.fir3.iwan.io.wasm.models.sections

import dev.fir3.iwan.io.wasm.models.Export

data class ExportSection(val exports: List<Export>) : Section
