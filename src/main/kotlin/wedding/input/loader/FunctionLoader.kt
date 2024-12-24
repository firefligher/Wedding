package dev.fir3.wedding.input.loader

import dev.fir3.iwan.io.wasm.models.FunctionExport
import dev.fir3.iwan.io.wasm.models.FunctionImport
import dev.fir3.iwan.io.wasm.models.Module
import dev.fir3.wedding.external.resolveName
import dev.fir3.wedding.external.toIntOrThrow
import dev.fir3.wedding.input.model.function.DefinedUnlinkedFunction
import dev.fir3.wedding.input.model.function.UnlinkedFunction
import dev.fir3.wedding.input.model.function.ImportedUnlinkedFunction
import dev.fir3.wedding.linking.model.NamedModule

internal object FunctionLoader : Loader<UnlinkedFunction> {
    override fun load(
        namedModules: Iterable<NamedModule>,
        destination: MutableSet<UnlinkedFunction>
    ) {
        for (namedModule in namedModules) load(namedModule, destination)
    }

    private fun load(
        namedModule: NamedModule,
        destination: MutableSet<UnlinkedFunction>
    ) {
        var nextFunctionIndex = 0u

        // All function declarations can be exported.

        val exports = namedModule
            .module
            .exports
            .filterIsInstance<FunctionExport>()

        // WebAssembly functions have implicitly assigned indices: Their order
        // in the binary implies their index. Also, imports come first, then
        // all defined functions follow.
        //
        // Handle the imports.

        for (import in namedModule.module.imports) {
            if (import !is FunctionImport) continue

            val index = nextFunctionIndex++
            val exportName = exports.resolveName(index)
            val isStart = namedModule.module.isStart(index)
            val type = namedModule.module.resolveType(import.typeIndex)

            destination += ImportedUnlinkedFunction(
                exportName = exportName,
                functionName = import.name,
                index = index,
                isStart = isStart,
                module = namedModule.name,
                sourceModule = import.module,
                type = type
            )
        }

        // Handle the definitions.
        //
        // The function type declaration and the corresponding body are
        // stored at separate locations in the WebAssembly binary. Hence, we
        // zip them together.

        val definitions = namedModule
            .module
            .functions
            .zip(namedModule.module.codes)

        for ((typeIndex, code) in definitions) {
            val index = nextFunctionIndex++
            val exportName = exports.resolveName(index)
            val isStart = namedModule.module.isStart(index)
            val type = namedModule.module.resolveType(typeIndex)

            destination += DefinedUnlinkedFunction(
                exportName = exportName,
                expression = code.body,
                index = index,
                isStart = isStart,
                locals = code.locals,
                module = namedModule.name,
                type = type
            )
        }
    }
}

private fun List<FunctionExport>.resolveName(index: UInt) =
    resolveName(index, FunctionExport::functionIndex)

private fun Module.isStart(index: UInt) = startFunction
    ?.let { startFunction -> startFunction == index }
    ?: false

private fun Module.resolveType(index: UInt) = types[index.toIntOrThrow()]
