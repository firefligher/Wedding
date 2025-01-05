package dev.fir3.wedding.linker.sanity

import dev.fir3.wedding.linker.pool.Object

data class DuplicateExport(
    val name: String,
    val objects: Set<Object>
)
