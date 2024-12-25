package dev.fir3.wedding.linking.renamer

import dev.fir3.wedding.input.model.MutableInputContainer
import dev.fir3.wedding.input.model.global.DefinedUnlinkedGlobal
import dev.fir3.wedding.input.model.identifier.ExportedGlobalIdentifier
import dev.fir3.wedding.input.model.identifier.identifier

internal object ExportedGlobalRenamer : AbstractRenamer<
        ExportedGlobalIdentifier,
        DefinedUnlinkedGlobal
>(DefinedUnlinkedGlobal::class) {
    override fun rename(
        obj: DefinedUnlinkedGlobal,
        newIdentifier: ExportedGlobalIdentifier
    ) = obj.copy(
        exportName = newIdentifier.global,
        module = newIdentifier.module,
        type = newIdentifier.type
    )

    override fun resolveIdentifier(obj: DefinedUnlinkedGlobal) = obj.identifier
    override fun resolveObjectSet(
        container: MutableInputContainer
    ) = container.globals
}
