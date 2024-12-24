package dev.fir3.wedding.linking.relocator

import dev.fir3.wedding.input.model.memory.DefinedUnlinkedMemory
import dev.fir3.wedding.input.model.memory.ImportedUnlinkedMemory
import dev.fir3.wedding.input.model.memory.UnlinkedMemory
import dev.fir3.wedding.linking.model.memory.DefinedRelocatedMemory
import dev.fir3.wedding.linking.model.memory.ImportedRelocatedMemory
import dev.fir3.wedding.linking.model.memory.RelocatedMemory

internal object MemoryRelocator : AbstractExportableRelocator<
        UnlinkedMemory,
        DefinedUnlinkedMemory,
        ImportedUnlinkedMemory,
        RelocatedMemory
>(DefinedUnlinkedMemory::class, ImportedUnlinkedMemory::class) {
    override fun deriveOutputElement(
        input: UnlinkedMemory,
        outputModuleName: String,
        relocatedIndex: UInt
    ) = when (input) {
        is DefinedUnlinkedMemory -> DefinedRelocatedMemory(
            exportName = input.exportName,
            index = relocatedIndex,
            module = outputModuleName,
            originalModule = input.module,
            type = input.type
        )

        is ImportedUnlinkedMemory -> ImportedRelocatedMemory(
            exportName = input.exportName,
            index = relocatedIndex,
            memoryName = input.memoryName,
            module = outputModuleName,
            originalModule = input.module,
            sourceModule = input.sourceModule,
            type = input.type
        )
    }

    override fun isLinkable(
        import: ImportedUnlinkedMemory,
        definition: DefinedUnlinkedMemory
    ) = import.sourceModule == definition.module &&
            import.memoryName == definition.exportName
}
