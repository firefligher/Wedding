package dev.fir3.wedding.linking.relocator

import dev.fir3.wedding.input.model.element.ActiveUnlinkedElement
import dev.fir3.wedding.input.model.element.DeclarativeUnlinkedElement
import dev.fir3.wedding.input.model.element.PassiveUnlinkedElement
import dev.fir3.wedding.input.model.element.UnlinkedElement
import dev.fir3.wedding.linking.model.element.ActiveRelocatedElement
import dev.fir3.wedding.linking.model.element.DeclarativeRelocatedElement
import dev.fir3.wedding.linking.model.element.PassiveRelocatedElement
import dev.fir3.wedding.linking.model.element.RelocatedElement

internal object ElementRelocator :
    AbstractIndexedRelocator<UnlinkedElement, RelocatedElement>() {
    override fun deriveOutputElement(
        input: UnlinkedElement,
        outputModuleName: String,
        relocatedIndex: UInt
    ) = when (input) {
        is ActiveUnlinkedElement -> ActiveRelocatedElement(
            index = relocatedIndex,
            initializers = input.initializers,
            module = outputModuleName,
            offset = input.offset,
            originalModule = input.module,
            tableIndex = input.tableIndex,
            type = input.type
        )

        is DeclarativeUnlinkedElement -> DeclarativeRelocatedElement(
            index = relocatedIndex,
            initializers = input.initializers,
            module = outputModuleName,
            originalModule = input.module,
            type = input.type
        )

        is PassiveUnlinkedElement -> PassiveRelocatedElement(
            index = relocatedIndex,
            initializers = input.initializers,
            module = outputModuleName,
            originalModule = input.module,
            type = input.type
        )
    }
}
