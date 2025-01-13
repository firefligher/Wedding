package dev.fir3.wedding.cli

import dev.fir3.wedding.linker.pool.ExportIdentifier

data class Renaming(
    val identifier: ExportIdentifier,
    val newName: String
)
