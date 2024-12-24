package dev.fir3.wedding.input.loader

import dev.fir3.wedding.linking.model.NamedModule

internal interface Loader<TObject> {
    fun load(
        namedModules: Iterable<NamedModule>,
        destination: MutableSet<TObject>
    )
}
