package dev.fir3.wedding.linking.relocator

import dev.fir3.wedding.input.model.data.ActiveUnlinkedData
import dev.fir3.wedding.input.model.data.PassiveUnlinkedData
import dev.fir3.wedding.input.model.data.UnlinkedData
import dev.fir3.wedding.linking.model.data.ActiveRelocatedData
import dev.fir3.wedding.linking.model.data.PassiveRelocatedData
import dev.fir3.wedding.linking.model.data.RelocatedData

internal object DataRelocator :
    AbstractIndexedRelocator<UnlinkedData, RelocatedData>() {
    override fun deriveOutputElement(
        input: UnlinkedData,
        outputModuleName: String,
        relocatedIndex: UInt
    ) = when (input) {
        is ActiveUnlinkedData -> ActiveRelocatedData(
            index = input.index,
            initializers = input.initializers,
            memoryIndex = input.memoryIndex,
            module = outputModuleName,
            offset = input.offset,
            originalModule = input.module
        )
        is PassiveUnlinkedData -> PassiveRelocatedData(
            index = input.index,
            initializers = input.initializers,
            module = outputModuleName,
            originalModule = input.module
        )
    }
}
