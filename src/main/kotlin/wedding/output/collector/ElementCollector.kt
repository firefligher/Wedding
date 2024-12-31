package dev.fir3.wedding.output.collector

import dev.fir3.iwan.io.wasm.models.ActiveElement
import dev.fir3.iwan.io.wasm.models.DeclarativeElement
import dev.fir3.iwan.io.wasm.models.PassiveElement
import dev.fir3.wedding.common.model.Indexed
import dev.fir3.wedding.linking.model.RelocationContainer
import dev.fir3.wedding.linking.model.element.ActiveRelocatedElement
import dev.fir3.wedding.linking.model.element.DeclarativeRelocatedElement
import dev.fir3.wedding.linking.model.element.PassiveRelocatedElement
import dev.fir3.wedding.output.model.MutableOutputContainer

internal object ElementCollector : Collector {
    override fun collect(
        source: RelocationContainer,
        destination: MutableOutputContainer
    ) = source.elements.sortedBy(Indexed::index).map { element ->
        when (element) {
            is ActiveRelocatedElement -> ActiveElement(
                type = element.type,
                initializers = element.initializers,
                table = element.tableIndex,
                offset = element.offset
            )

            is DeclarativeRelocatedElement -> DeclarativeElement(
                type = element.type,
                initializers = element.initializers
            )

            is PassiveRelocatedElement -> PassiveElement(
                type = element.type,
                initializers = element.initializers
            )
        }
    }.forEach(destination.elements::add)
}
