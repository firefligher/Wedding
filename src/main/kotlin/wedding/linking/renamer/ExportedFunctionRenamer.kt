package dev.fir3.wedding.linking.renamer

import dev.fir3.wedding.input.model.MutableInputContainer
import dev.fir3.wedding.input.model.function.DefinedUnlinkedFunction
import dev.fir3.wedding.input.model.identifier.ExportedFunctionIdentifier
import dev.fir3.wedding.input.model.identifier.identifier

internal object ExportedFunctionRenamer : AbstractRenamer<
        ExportedFunctionIdentifier,
        DefinedUnlinkedFunction
>(DefinedUnlinkedFunction::class)  {
    override fun rename(
        obj: DefinedUnlinkedFunction,
        newIdentifier: ExportedFunctionIdentifier
    ) = obj.copy(
        exportName = newIdentifier.function,
        module = newIdentifier.module,
        type = newIdentifier.type
    )

    override fun resolveIdentifier(
        obj: DefinedUnlinkedFunction
    ) = obj.identifier

    override fun resolveObjectSet(
        container: MutableInputContainer
    ) = container.functions
}
