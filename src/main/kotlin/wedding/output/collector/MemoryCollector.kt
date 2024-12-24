package dev.fir3.wedding.output.collector

import dev.fir3.iwan.io.wasm.models.MemoryExport
import dev.fir3.iwan.io.wasm.models.MemoryImport
import dev.fir3.wedding.common.model.Indexed
import dev.fir3.wedding.linking.model.RelocationContainer
import dev.fir3.wedding.linking.model.memory.DefinedRelocatedMemory
import dev.fir3.wedding.linking.model.memory.ImportedRelocatedMemory
import dev.fir3.wedding.output.model.MutableOutputContainer

internal object MemoryCollector : Collector {
    override fun collect(
        source: RelocationContainer,
        destination: MutableOutputContainer
    ) {
        for (memory in source.memories.sortedBy(Indexed::index)) {
            when (memory) {
                is DefinedRelocatedMemory ->
                    destination.memories += memory.type

                is ImportedRelocatedMemory ->
                    destination.imports += MemoryImport(
                        module = memory.sourceModule,
                        name = memory.memoryName,
                        type = memory.type
                    )
            }

            memory.exportName?.let { exportedName ->
                destination.exports += MemoryExport(
                    name = exportedName,
                    memoryIndex = memory.index
                )
            }
        }
    }
}
