package dev.fir3.wedding.output.collector

import dev.fir3.iwan.io.wasm.models.ActiveData
import dev.fir3.iwan.io.wasm.models.PassiveData
import dev.fir3.wedding.common.model.Indexed
import dev.fir3.wedding.linking.model.RelocationContainer
import dev.fir3.wedding.linking.model.data.ActiveRelocatedData
import dev.fir3.wedding.linking.model.data.PassiveRelocatedData
import dev.fir3.wedding.output.model.MutableOutputContainer

internal object DataCollector : Collector {
    override fun collect(
        source: RelocationContainer,
        destination: MutableOutputContainer
    ) = source.datas.sortedBy(Indexed::index).map { data ->
        when (data) {
            is ActiveRelocatedData -> ActiveData(
                initializers = data.initializers,
                memoryIndex = data.memoryIndex,
                offset = data.offset
            )

            is PassiveRelocatedData -> PassiveData(
                initializers = data.initializers
            )
        }
    }.forEach(destination.data::add)
}
