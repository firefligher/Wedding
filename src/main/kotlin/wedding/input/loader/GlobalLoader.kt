package dev.fir3.wedding.input.loader

import dev.fir3.iwan.io.wasm.models.GlobalExport
import dev.fir3.iwan.io.wasm.models.GlobalImport
import dev.fir3.wedding.external.resolveName
import dev.fir3.wedding.input.model.global.DefinedUnlinkedGlobal
import dev.fir3.wedding.input.model.global.UnlinkedGlobal
import dev.fir3.wedding.input.model.global.ImportedUnlinkedGlobal
import dev.fir3.wedding.linking.model.NamedModule

internal object GlobalLoader : Loader<UnlinkedGlobal> {
    override fun load(
        namedModules: Iterable<NamedModule>,
        destination: MutableSet<UnlinkedGlobal>
    ) {
        for (namedModule in namedModules) load(namedModule, destination)
    }

    private fun load(
        namedModule: NamedModule,
        destination: MutableSet<UnlinkedGlobal>
    ) {
        var nextGlobalIndex = 0u

        // All global declarations can be exported.

        val exports = namedModule
            .module
            .exports
            .filterIsInstance<GlobalExport>()

        // WebAssembly globals have implicitly assigned indices: Their order in
        // the binary implies their index. Also, imports come first, then all
        // defined globals follow.
        //
        // Handle the imports.

        for (import in namedModule.module.imports) {
            if (import !is GlobalImport) continue

            val index = nextGlobalIndex++
            val exportName = exports.resolveName(index)

            destination += ImportedUnlinkedGlobal(
                exportName = exportName,
                globalName = import.name,
                index = index,
                module = namedModule.name,
                sourceModule = import.module,
                type = import.type
            )
        }

        // Handle the definitions.

        for (global in namedModule.module.globals) {
            val index = nextGlobalIndex++
            val exportName = exports.resolveName(index)

            destination += DefinedUnlinkedGlobal(
                exportName = exportName,
                index = index,
                initializer = global.initializer,
                module = namedModule.name,
                type = global.type
            )
        }
    }
}

private fun List<GlobalExport>.resolveName(index: UInt) =
    resolveName(index, GlobalExport::globalIndex)
