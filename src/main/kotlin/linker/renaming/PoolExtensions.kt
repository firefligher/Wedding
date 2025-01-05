package dev.fir3.wedding.linker.renaming

import dev.fir3.wedding.linker.pool.*

fun Pool.rename(identifier: Identifier, newName: String) =
    resolve(identifier)?.let { `object` ->
        `object`[AssignedName::class] = AssignedName(newName)
        true
    } ?: false
