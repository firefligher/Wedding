package dev.fir3.wedding.linking.relocator

import dev.fir3.iwan.io.wasm.models.TableType
import dev.fir3.wedding.input.model.table.DefinedUnlinkedTable
import dev.fir3.wedding.input.model.table.ImportedUnlinkedTable
import dev.fir3.wedding.input.model.table.UnlinkedTable
import dev.fir3.wedding.linking.model.table.DefinedRelocatedTable
import dev.fir3.wedding.linking.model.table.ImportedRelocatedTable
import dev.fir3.wedding.linking.model.table.RelocatedTable

internal object TableRelocator : AbstractExportableRelocator<
        TableType,
        UnlinkedTable,
        DefinedUnlinkedTable,
        ImportedUnlinkedTable,
        RelocatedTable
>(DefinedUnlinkedTable::class, ImportedUnlinkedTable::class) {
    override fun deriveOutputElement(
        input: UnlinkedTable,
        outputModuleName: String,
        relocatedIndex: UInt
    ) = when (input) {
        is DefinedUnlinkedTable -> DefinedRelocatedTable(
            exportName = input.exportName,
            index = relocatedIndex,
            module = outputModuleName,
            originalModule = input.module,
            type = input.type
        )

        is ImportedUnlinkedTable -> ImportedRelocatedTable(
            exportName = input.exportName,
            index = relocatedIndex,
            module = outputModuleName,
            originalModule = input.module,
            sourceModule = input.sourceModule,
            tableName = input.tableName,
            type = input.type
        )
    }

    override fun isLinkable(
        import: ImportedUnlinkedTable,
        definition: DefinedUnlinkedTable
    ) = import.sourceModule == definition.module &&
            import.tableName == definition.exportName &&
            import.type == definition.type
}
