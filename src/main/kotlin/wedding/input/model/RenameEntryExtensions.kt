package dev.fir3.wedding.input.model

import dev.fir3.wedding.input.model.identifier.*

internal val RenameEntry.isValid: Boolean get() {
    val original = this.originalIdentifier
    val new = this.newIdentifier

    return when (original) {
        is ExportedFunctionIdentifier ->
            (new as? ExportedFunctionIdentifier)?.let { safeNew ->
                safeNew.type == original.type
            } ?: false

        is ExportedGlobalIdentifier ->
            (new as? ExportedGlobalIdentifier)?.let { safeNew ->
                safeNew.type == original.type
            } ?: false

        is ExportedMemoryIdentifier ->
            (new as? ExportedMemoryIdentifier)?.let { safeNew ->
                safeNew.type == original.type
            } ?: false

        is ImportedFunctionIdentifier ->
            (new as? ImportedFunctionIdentifier)?.let { safeNew ->
                safeNew.type == original.type
            } ?: false

        is ImportedGlobalIdentifier ->
            (new as? ImportedGlobalIdentifier)?.let { safeNew ->
                safeNew.type == original.type
            } ?: false

        is ImportedMemoryIdentifier ->
            (new as? ImportedMemoryIdentifier)?.let { safeNew ->
                safeNew.type == original.type
            } ?: false
    }
}
