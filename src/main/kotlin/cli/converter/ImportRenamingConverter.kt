package dev.fir3.wedding.cli.converter

import dev.fir3.wedding.cli.ImportRenaming
import dev.fir3.wedding.linker.pool.ImportIdentifier

object ImportRenamingConverter :
    AbstractImportRenamingValueConverter<ImportRenaming>() {
    override fun convert(
        identifier: ImportIdentifier,
        name: String
    ) = ImportRenaming(identifier, name)

    override fun valueType() = ImportRenaming::class.java
    override fun valuePattern() = "module:source-module.name=new-name"
}
