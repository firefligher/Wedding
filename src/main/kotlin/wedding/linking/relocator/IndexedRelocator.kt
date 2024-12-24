package dev.fir3.wedding.linking.relocator

import dev.fir3.wedding.common.model.Indexed
import dev.fir3.wedding.linking.model.MutableRelocationTable

internal interface IndexedRelocator<
        TInputElement : Indexed,
        TOutputElement : Indexed
> {
    fun relocate(
        relocations: MutableRelocationTable,
        input: Iterable<TInputElement>,
        output: MutableSet<TOutputElement>,
        outputModuleName: String
    )
}
