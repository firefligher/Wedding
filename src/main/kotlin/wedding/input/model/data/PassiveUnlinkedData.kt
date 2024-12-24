package dev.fir3.wedding.input.model.data

internal data class PassiveUnlinkedData(
    override val index: UInt,
    override val initializers: List<Byte>,
    override val module: String
) : UnlinkedData
