package dev.fir3.wedding.linker.linking

import dev.fir3.wedding.linker.pool.Identifier


data class Conflict(
    val export: Identifier,
    val import: Identifier
)
