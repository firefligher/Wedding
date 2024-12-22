package dev.fir3.iwan.io.wasm.models.instructions.blockTypes

import dev.fir3.iwan.io.wasm.models.valueTypes.ValueType

data class InlineBlockType(val valueType: ValueType): BlockType
