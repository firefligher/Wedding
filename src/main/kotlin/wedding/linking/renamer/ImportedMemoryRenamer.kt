package dev.fir3.wedding.linking.renamer

import dev.fir3.wedding.input.model.MutableInputContainer
import dev.fir3.wedding.input.model.identifier.ImportedMemoryIdentifier
import dev.fir3.wedding.input.model.identifier.identifier
import dev.fir3.wedding.input.model.memory.ImportedUnlinkedMemory

internal object ImportedMemoryRenamer : AbstractRenamer<
        ImportedMemoryIdentifier,
        ImportedUnlinkedMemory
>(ImportedUnlinkedMemory::class) {
    override fun rename(
        obj: ImportedUnlinkedMemory,
        newIdentifier: ImportedMemoryIdentifier
    ) = obj.copy(
        memoryName = newIdentifier.memory,
        module = newIdentifier.module,
        sourceModule = newIdentifier.sourceModule,
        type = newIdentifier.type
    )

    override fun resolveIdentifier(obj: ImportedUnlinkedMemory) =
        obj.identifier

    override fun resolveObjectSet(container: MutableInputContainer) =
        container.memories
}
