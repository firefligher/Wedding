package dev.fir3.wedding.output.collector

import dev.fir3.wedding.linking.model.RelocationContainer
import dev.fir3.wedding.output.model.MutableOutputContainer

internal interface Collector {
    fun collect(
        source: RelocationContainer,
        destination: MutableOutputContainer
    )
}
