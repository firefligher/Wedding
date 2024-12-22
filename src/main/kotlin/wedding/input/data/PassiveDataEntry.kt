package dev.fir3.wedding.input.data

internal data class PassiveDataEntry(
    override val index: UInt,
    override val initializers: List<Byte>,
    override val moduleName: String
) : DataEntry
