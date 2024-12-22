package dev.fir3.iwan.io.wasm.models

import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType

data class TableType(
    val elementType: ReferenceType,
    val minimum: UInt,
    val maximum: UInt?
)
