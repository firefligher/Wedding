package dev.fir3.iwan.io.wasm.models

import dev.fir3.iwan.io.wasm.models.instructions.Instruction

data class Expression(val body: List<Instruction>)
