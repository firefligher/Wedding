package dev.fir3.wedding.output.collector

import dev.fir3.iwan.io.wasm.models.Code
import dev.fir3.iwan.io.wasm.models.FunctionExport
import dev.fir3.iwan.io.wasm.models.FunctionImport
import dev.fir3.wedding.Log
import dev.fir3.wedding.common.model.Indexed
import dev.fir3.wedding.linking.model.RelocationContainer
import dev.fir3.wedding.linking.model.function.DefinedRelocatedFunction
import dev.fir3.wedding.linking.model.function.ImportedRelocatedFunction
import dev.fir3.wedding.output.model.MutableOutputContainer

internal object FunctionCollector : Collector {
    override fun collect(
        source: RelocationContainer,
        destination: MutableOutputContainer
    ) {
        for (function in source.functions.sortedBy(Indexed::index)) {
            var typeIndex = destination.types.indexOf(function.type)

            if (typeIndex == -1) {
                typeIndex = destination.types.size
                destination.types.add(typeIndex, function.type)
            }

            val unsignedTypeIndex = typeIndex.toUInt()

            when (function) {
                is DefinedRelocatedFunction -> {
                    destination.functions += unsignedTypeIndex
                    destination.codes += Code(
                        locals = function.locals,
                        body = function.expression
                    )
                }

                is ImportedRelocatedFunction ->
                    destination.imports += FunctionImport(
                        module = function.sourceModule,
                        name = function.functionName,
                        typeIndex = unsignedTypeIndex
                    )
            }

            function.exportName?.let { exportName ->
                Log.d("Exporting function $exportName")

                destination.exports += FunctionExport(
                    name = exportName,
                    functionIndex = function.index
                )
            }

            if (function.isStart) {
                check(destination.startFunction == null)
                destination.startFunction = function.index
            }
        }
    }
}
