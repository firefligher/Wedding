package dev.fir3.wedding.linking.model.data

internal data class PassiveRelocatedData(
    override val index: UInt,
    override val initializers: List<Byte>,
    override val module: String,
    override val originalModule: String
) : RelocatedData
