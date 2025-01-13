package dev.fir3.wedding.cli.converter

import dev.fir3.wedding.cli.ImportModuleRenaming
import dev.fir3.wedding.linker.pool.ImportIdentifier

object ImportModuleRenamingConverter :
    AbstractImportRenamingValueConverter<ImportModuleRenaming>() {
    override fun convert(
        identifier: ImportIdentifier,
        name: String
    ) = ImportModuleRenaming(identifier, name)

    override fun valueType() = ImportModuleRenaming::class.java
    override fun valuePattern() = "module:source-module.name=new-source-module"
}