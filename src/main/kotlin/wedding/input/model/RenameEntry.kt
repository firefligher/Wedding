package dev.fir3.wedding.input.model

import dev.fir3.wedding.input.model.identifier.Identifier

internal data class RenameEntry<TIdentifier : Identifier<*>>(
    val newIdentifier: TIdentifier,
    val originalIdentifier: TIdentifier
)
