package dev.fir3.wedding.linking.renamer

import dev.fir3.wedding.input.model.MutableInputContainer
import dev.fir3.wedding.input.model.function.ImportedUnlinkedFunction
import dev.fir3.wedding.input.model.identifier.ImportedFunctionIdentifier
import dev.fir3.wedding.input.model.identifier.identifier

internal object ImportedFunctionRenamer : AbstractRenamer<
        ImportedFunctionIdentifier,
        ImportedUnlinkedFunction
>(ImportedUnlinkedFunction::class) {
    override fun rename(
        obj: ImportedUnlinkedFunction,
        newIdentifier: ImportedFunctionIdentifier
    ) = obj.copy(
        functionName = newIdentifier.function,
        module = newIdentifier.module,
        sourceModule = newIdentifier.sourceModule,
        type = newIdentifier.type
    )

    override fun resolveIdentifier(obj: ImportedUnlinkedFunction) =
        obj.identifier

    override fun resolveObjectSet(container: MutableInputContainer) =
        container.functions
}
