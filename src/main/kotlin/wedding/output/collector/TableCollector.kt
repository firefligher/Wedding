package dev.fir3.wedding.output.collector

import dev.fir3.iwan.io.wasm.models.TableExport
import dev.fir3.iwan.io.wasm.models.TableImport
import dev.fir3.wedding.Log
import dev.fir3.wedding.common.model.Indexed
import dev.fir3.wedding.linking.model.RelocationContainer
import dev.fir3.wedding.linking.model.table.DefinedRelocatedTable
import dev.fir3.wedding.linking.model.table.ImportedRelocatedTable
import dev.fir3.wedding.output.model.MutableOutputContainer

internal object TableCollector : Collector {
    override fun collect(
        source: RelocationContainer,
        destination: MutableOutputContainer
    ) {
        for (table in source.tables.sortedBy(Indexed::index)) {
            when (table) {
                is DefinedRelocatedTable -> destination.tables += table.type
                is ImportedRelocatedTable ->
                    destination.imports += TableImport(
                        module = table.sourceModule,
                        name = table.tableName,
                        type = table.type
                    )
            }

            table.exportName?.let { exportName ->
                Log.d("Export table %s", exportName)

                destination.exports += TableExport(
                    name = exportName,
                    tableIndex = table.index
                )
            }
        }
    }
}
