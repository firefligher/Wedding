package dev.fir3.wedding.linker.renaming

import dev.fir3.wedding.linker.pool.*

fun Pool.rename(identifier: Identifier, newName: String) =
    resolve(identifier)?.let { `object` ->
        `object`[AssignedName::class] = AssignedName(newName)
        true
    } ?: false

fun Pool.renameImport(identifier: ImportIdentifier, newName: String) =
    resolve(identifier)?.let { `object` ->
        `object`[AssignedImportName::class] = AssignedImportName(newName)
        true
    } ?: false

fun Pool.renameImportModule(identifier: ImportIdentifier, newModule: String) =
    resolve(identifier)?.let { `object` ->
        `object`[AssignedImportModule::class] = AssignedImportModule(newModule)
        true
    } ?: false
