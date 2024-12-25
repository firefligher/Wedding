package dev.fir3.wedding.linking.renamer

import dev.fir3.wedding.input.model.MutableInputContainer
import dev.fir3.wedding.input.model.global.ImportedUnlinkedGlobal
import dev.fir3.wedding.input.model.identifier.ImportedGlobalIdentifier
import dev.fir3.wedding.input.model.identifier.identifier

internal object ImportedGlobalRenamer : AbstractRenamer<
        ImportedGlobalIdentifier,
        ImportedUnlinkedGlobal
>(ImportedUnlinkedGlobal::class) {
    override fun rename(
        obj: ImportedUnlinkedGlobal,
        newIdentifier: ImportedGlobalIdentifier
    ) = obj.copy(
        globalName = newIdentifier.global,
        module = newIdentifier.module,
        sourceModule = newIdentifier.sourceModule,
        type = newIdentifier.type
    )

    override fun resolveIdentifier(obj: ImportedUnlinkedGlobal) =
        obj.identifier

    override fun resolveObjectSet(container: MutableInputContainer) =
        container.globals
}
