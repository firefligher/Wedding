package dev.fir3.wedding.linking.model

import dev.fir3.iwan.io.wasm.models.Module

data class NamedModule(
    val name: String,
    val module: Module
)
