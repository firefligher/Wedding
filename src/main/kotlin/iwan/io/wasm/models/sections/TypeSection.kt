package dev.fir3.iwan.io.wasm.models.sections

import dev.fir3.iwan.io.wasm.models.FunctionType

data class TypeSection(
    val types: List<FunctionType>
) : Section
