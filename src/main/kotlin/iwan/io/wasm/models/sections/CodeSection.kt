package dev.fir3.iwan.io.wasm.models.sections

import dev.fir3.iwan.io.wasm.models.Code

data class CodeSection(val codes: List<Code>): Section
