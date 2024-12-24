package dev.fir3.wedding.input.loader

import dev.fir3.iwan.io.wasm.models.MemoryExport
import dev.fir3.iwan.io.wasm.models.MemoryImport
import dev.fir3.wedding.external.resolveName
import dev.fir3.wedding.input.model.memory.DefinedUnlinkedMemory
import dev.fir3.wedding.input.model.memory.ImportedUnlinkedMemory
import dev.fir3.wedding.input.model.memory.UnlinkedMemory
import dev.fir3.wedding.linking.model.NamedModule

internal object MemoryLoader : Loader<UnlinkedMemory> {
    override fun load(
        namedModules: Iterable<NamedModule>,
        destination: MutableSet<UnlinkedMemory>
    ) {
        for (namedModule in namedModules) load(namedModule, destination)
    }

    private fun load(
        namedModule: NamedModule,
        destination: MutableSet<UnlinkedMemory>
    ) {
        var nextMemoryIndex = 0u

        // All module declarations can be exported.

        val exports = namedModule
            .module
            .exports
            .filterIsInstance<MemoryExport>()

        // WebAssembly memories have implicitly assigned indices: Their order
        // in the binary implies their index. Also, imports come first, then
        // all defined memories follow.
        //
        // Handle the imports.

        for (import in namedModule.module.imports) {
            if (import !is MemoryImport) continue

            val index = nextMemoryIndex++
            val exportName = exports.resolveName(index)

            destination += ImportedUnlinkedMemory(
                exportName = exportName,
                index = index,
                memoryName = import.name,
                module = namedModule.name,
                sourceModule = import.module,
                type = import.type
            )
        }

        // Handle the definitions.

        for (memoryType in namedModule.module.memories) {
            val index = nextMemoryIndex++
            val exportName = exports.resolveName(index)

            destination += DefinedUnlinkedMemory(
                exportName = exportName,
                index = index,
                module = namedModule.name,
                type = memoryType
            )
        }
    }
}

private fun List<MemoryExport>.resolveName(index: UInt) =
    resolveName(index, MemoryExport::memoryIndex)
