package dev.fir3.wedding.linking.renamer

import dev.fir3.wedding.input.model.MutableInputContainer
import dev.fir3.wedding.input.model.identifier.ExportedMemoryIdentifier
import dev.fir3.wedding.input.model.identifier.identifier
import dev.fir3.wedding.input.model.memory.DefinedUnlinkedMemory

internal object ExportedMemoryRenamer : AbstractRenamer<
        ExportedMemoryIdentifier,
        DefinedUnlinkedMemory
>(DefinedUnlinkedMemory::class) {
    override fun rename(
        obj: DefinedUnlinkedMemory,
        newIdentifier: ExportedMemoryIdentifier
    ) = obj.copy(
        exportName = newIdentifier.memory,
        module = newIdentifier.module,
        type = newIdentifier.type
    )

    override fun resolveIdentifier(obj: DefinedUnlinkedMemory) = obj.identifier
    override fun resolveObjectSet(
        container: MutableInputContainer
    ) = container.memories
}
