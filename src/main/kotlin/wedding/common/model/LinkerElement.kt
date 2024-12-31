package dev.fir3.wedding.common.model

import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType

internal interface LinkerElement : Indexed {
    val initializers: List<Expression>
    val type: ReferenceType
}
