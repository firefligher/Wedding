package dev.fir3.wedding.linking.relocator

import dev.fir3.wedding.input.model.global.DefinedUnlinkedGlobal
import dev.fir3.wedding.input.model.global.ImportedUnlinkedGlobal
import dev.fir3.wedding.input.model.global.UnlinkedGlobal
import dev.fir3.wedding.linking.model.global.DefinedRelocatedGlobal
import dev.fir3.wedding.linking.model.global.ImportedRelocatedGlobal
import dev.fir3.wedding.linking.model.global.RelocatedGlobal

internal object GlobalRelocator : AbstractExportableRelocator<
        UnlinkedGlobal,
        DefinedUnlinkedGlobal,
        ImportedUnlinkedGlobal,
        RelocatedGlobal
>(DefinedUnlinkedGlobal::class, ImportedUnlinkedGlobal::class) {
    override fun deriveOutputElement(
        input: UnlinkedGlobal,
        outputModuleName: String,
        relocatedIndex: UInt
    ) = when (input) {
        is DefinedUnlinkedGlobal -> DefinedRelocatedGlobal(
            exportName = input.exportName,
            index = relocatedIndex,
            initializer = input.initializer,
            module = outputModuleName,
            originalModule = input.module,
            type = input.type
        )

        is ImportedUnlinkedGlobal -> ImportedRelocatedGlobal(
            exportName = input.exportName,
            globalName = input.globalName,
            index = relocatedIndex,
            module = outputModuleName,
            originalModule = input.module,
            sourceModule = input.sourceModule,
            type = input.type
        )
    }

    override fun isLinkable(
        import: ImportedUnlinkedGlobal,
        definition: DefinedUnlinkedGlobal
    ) = import.sourceModule == definition.module &&
            import.globalName == definition.exportName
}
