package dev.fir3.wedding.cli.converter

import dev.fir3.wedding.cli.GlobalDefinition
import dev.fir3.wedding.cli.StorableType
import joptsimple.ValueConverter

object GlobalDefinitionConverter : ValueConverter<GlobalDefinition> {
    override fun convert(p0: String): GlobalDefinition {
        var name = ""
        var typeStr = ""
        var mutableStr = ""
        var initialValueStr = ""
        var escaped = false
        var state = State.IN_NAME

        for (char in p0) {
            if (char == '\\' && !escaped) {
                escaped = true
                continue
            }

            if (char == ':' && !escaped) {
                state = when (state) {
                    State.IN_MUTABLE -> State.IN_INITIAL_VALUE
                    State.IN_NAME -> State.IN_TYPE
                    State.IN_TYPE -> State.IN_MUTABLE
                    else -> state
                }

                continue
            }

            when (state) {
                State.IN_INITIAL_VALUE -> initialValueStr += char
                State.IN_MUTABLE -> mutableStr += char
                State.IN_NAME -> name += char
                State.IN_TYPE -> typeStr += char
            }
        }

        val type = StorableType.valueOf(typeStr)

        return GlobalDefinition(
            when (type) {
                StorableType.FLOAT32 -> initialValueStr.toFloat()
                StorableType.FLOAT64 -> initialValueStr.toDouble()
                StorableType.INT8S -> initialValueStr.toInt()
                StorableType.INT8U -> initialValueStr.toInt()
                StorableType.INT16S -> initialValueStr.toInt()
                StorableType.INT16U -> initialValueStr.toInt()
                StorableType.INT32 -> initialValueStr.toInt()
                StorableType.INT64 -> initialValueStr.toLong()
            },
            isMutable = mutableStr.toBoolean(),
            name = name,
            type = type
        )
    }

    override fun valueType() = GlobalDefinition::class.java
    override fun valuePattern() = "name:type:mutable:initial-value"

    private enum class State {
        IN_INITIAL_VALUE,
        IN_MUTABLE,
        IN_NAME,
        IN_TYPE
    }
}
