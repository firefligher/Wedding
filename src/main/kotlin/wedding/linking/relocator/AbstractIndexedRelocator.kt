package dev.fir3.wedding.linking.relocator

import dev.fir3.wedding.common.model.Indexed
import dev.fir3.wedding.linking.model.MutableRelocationTable

internal abstract class AbstractIndexedRelocator<
        TInputElement : Indexed,
        TOutputElement : Indexed
> : IndexedRelocator<TInputElement, TOutputElement> {
    override fun relocate(
        relocations: MutableRelocationTable,
        input: Iterable<TInputElement>,
        output: MutableSet<TOutputElement>,
        outputModuleName: String
    ) = input.mapIndexed { relocatedIndex, inputElement ->
        val unsignedRelocatedIndex = relocatedIndex.toUInt()
        relocations[inputElement.module, inputElement.index] =
            unsignedRelocatedIndex

        deriveOutputElement(
            inputElement,
            outputModuleName,
            unsignedRelocatedIndex
        )
    }.forEach(output::add)

    protected abstract fun deriveOutputElement(
        input: TInputElement,
        outputModuleName: String,
        relocatedIndex: UInt
    ): TOutputElement
}
