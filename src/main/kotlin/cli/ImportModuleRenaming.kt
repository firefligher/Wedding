package dev.fir3.wedding.cli

import dev.fir3.wedding.linker.pool.ImportIdentifier

data class ImportModuleRenaming(
    val identifier: ImportIdentifier,
    val newModule: String
)
