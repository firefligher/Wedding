package dev.fir3.wedding.output.collector

import dev.fir3.iwan.io.wasm.models.Global
import dev.fir3.iwan.io.wasm.models.GlobalExport
import dev.fir3.iwan.io.wasm.models.GlobalImport
import dev.fir3.wedding.common.model.Indexed
import dev.fir3.wedding.linking.model.RelocationContainer
import dev.fir3.wedding.linking.model.global.DefinedRelocatedGlobal
import dev.fir3.wedding.linking.model.global.ImportedRelocatedGlobal
import dev.fir3.wedding.output.model.MutableOutputContainer

internal object GlobalCollector : Collector {
    override fun collect(
        source: RelocationContainer,
        destination: MutableOutputContainer
    ) {
        for (global in source.globals.sortedBy(Indexed::index)) {
            when (global) {
                is DefinedRelocatedGlobal -> destination.globals += Global(
                    type = global.type,
                    initializer = global.initializer
                )

                is ImportedRelocatedGlobal ->
                    destination.imports += GlobalImport(
                        module = global.sourceModule,
                        name = global.globalName,
                        type = global.type
                    )
            }

            global.exportName?.let { exportName ->
                destination.exports += GlobalExport(
                    name = exportName,
                    globalIndex = global.index
                )
            }
        }
    }
}
