package dev.fir3.wedding.input.loader

import dev.fir3.iwan.io.wasm.models.ActiveData
import dev.fir3.iwan.io.wasm.models.PassiveData
import dev.fir3.wedding.input.model.data.ActiveUnlinkedData
import dev.fir3.wedding.input.model.data.UnlinkedData
import dev.fir3.wedding.input.model.data.PassiveUnlinkedData
import dev.fir3.wedding.linking.model.NamedModule

internal object DataLoader : Loader<UnlinkedData> {
    override fun load(
        namedModules: Iterable<NamedModule>,
        destination: MutableSet<UnlinkedData>
    ) {
        for (namedModule in namedModules) load(namedModule, destination)
    }

    private fun load(
        namedModule: NamedModule,
        destination: MutableSet<UnlinkedData>
    ) {
        var nextDataIndex = 0u

        for (data in namedModule.module.data) {
            destination += when (data) {
                is ActiveData -> ActiveUnlinkedData(
                    index = nextDataIndex++,
                    initializers = data.initializers,
                    memoryIndex = data.memoryIndex,
                    module = namedModule.name,
                    offset = data.offset
                )
                is PassiveData -> PassiveUnlinkedData(
                    index = nextDataIndex++,
                    initializers = data.initializers,
                    module = namedModule.name
                )
            }
        }
    }
}
