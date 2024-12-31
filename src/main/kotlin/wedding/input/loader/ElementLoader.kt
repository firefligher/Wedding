package dev.fir3.wedding.input.loader

import dev.fir3.iwan.io.wasm.models.ActiveElement
import dev.fir3.iwan.io.wasm.models.DeclarativeElement
import dev.fir3.iwan.io.wasm.models.PassiveElement
import dev.fir3.wedding.input.model.element.ActiveUnlinkedElement
import dev.fir3.wedding.input.model.element.DeclarativeUnlinkedElement
import dev.fir3.wedding.input.model.element.PassiveUnlinkedElement
import dev.fir3.wedding.input.model.element.UnlinkedElement
import dev.fir3.wedding.linking.model.NamedModule

internal object ElementLoader : Loader<UnlinkedElement> {
    override fun load(
        namedModules: Iterable<NamedModule>,
        destination: MutableSet<UnlinkedElement>
    ) {
        for (namedModule in namedModules) load(namedModule, destination)
    }

    private fun load(
        namedModule: NamedModule,
        destination: MutableSet<UnlinkedElement>
    ) {
        var nextElementIndex = 0u

        for (element in namedModule.module.elements) {
            destination += when (element) {
                is ActiveElement -> ActiveUnlinkedElement(
                    index = nextElementIndex++,
                    initializers = element.initializers,
                    module = namedModule.name,
                    offset = element.offset,
                    tableIndex = element.table,
                    type = element.type
                )
                is DeclarativeElement -> DeclarativeUnlinkedElement(
                    index = nextElementIndex++,
                    initializers = element.initializers,
                    module = namedModule.name,
                    type = element.type
                )
                is PassiveElement -> PassiveUnlinkedElement(
                    index = nextElementIndex++,
                    initializers = element.initializers,
                    module = namedModule.name,
                    type = element.type
                )
            }
        }
    }
}
