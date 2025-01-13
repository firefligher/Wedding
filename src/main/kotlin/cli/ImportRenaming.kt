package dev.fir3.wedding.cli

import dev.fir3.wedding.linker.pool.ImportIdentifier

data class ImportRenaming(
    val identifier: ImportIdentifier,
    val newName: String
)
