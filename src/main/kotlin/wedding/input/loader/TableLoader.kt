package dev.fir3.wedding.input.loader

import dev.fir3.iwan.io.wasm.models.TableExport
import dev.fir3.iwan.io.wasm.models.TableImport
import dev.fir3.wedding.external.resolveName
import dev.fir3.wedding.input.model.table.DefinedUnlinkedTable
import dev.fir3.wedding.input.model.table.ImportedUnlinkedTable
import dev.fir3.wedding.input.model.table.UnlinkedTable
import dev.fir3.wedding.linking.model.NamedModule

internal object TableLoader : Loader<UnlinkedTable> {
    override fun load(
        namedModules: Iterable<NamedModule>,
        destination: MutableSet<UnlinkedTable>
    ) {
        for (namedModule in namedModules) load(namedModule, destination)
    }

    private fun load(
        namedModule: NamedModule,
        destination: MutableSet<UnlinkedTable>
    ) {
        var nextTableIndex = 0u

        // All module declarations can be exported.

        val exports = namedModule
            .module
            .exports
            .filterIsInstance<TableExport>()

        // WebAssembly tables have implicitly assigned indices: Their order in
        // the binary implies their index. Also, imports come first, then all
        // defined tables follow.
        //
        // Handle the imports.

        for (import in namedModule.module.imports) {
            if (import !is TableImport) continue

            val index = nextTableIndex++
            val exportName = exports.resolveName(index)

            destination += ImportedUnlinkedTable(
                exportName = exportName,
                index = index,
                module = namedModule.name,
                sourceModule = import.module,
                tableName = import.name,
                type = import.type
            )
        }

        // Handle the definitions.

        for (tableType in namedModule.module.tables) {
            val index = nextTableIndex++
            val exportName = exports.resolveName(index)

            destination += DefinedUnlinkedTable(
                exportName = exportName,
                index = index,
                module = namedModule.name,
                type = tableType
            )
        }
    }
}

private fun List<TableExport>.resolveName(index: UInt) =
    resolveName(index, TableExport::tableIndex)
