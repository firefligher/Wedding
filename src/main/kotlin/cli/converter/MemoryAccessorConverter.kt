package dev.fir3.wedding.cli.converter

import dev.fir3.wedding.cli.MemoryAccessor
import dev.fir3.wedding.cli.StorableType
import joptsimple.ValueConverter

object MemoryAccessorConverter : ValueConverter<MemoryAccessor> {
    override fun convert(p0: String): MemoryAccessor {
        var getterName = ""
        var setterName = ""
        var addressStr = ""
        var typeStr = ""
        var escaped = false
        var state = State.IN_GETTER

        for (char in p0) {
            if (char == '\\' && !escaped) {
                escaped = true
                continue
            }

            if (char == ':' && !escaped) {
                state = when (state) {
                    State.IN_ADDRESS -> State.IN_TYPE
                    State.IN_GETTER -> State.IN_SETTER
                    State.IN_SETTER -> State.IN_ADDRESS
                    else -> state
                }

                continue
            }

            when (state) {
                State.IN_ADDRESS -> addressStr += char
                State.IN_GETTER -> getterName += char
                State.IN_SETTER -> setterName += char
                State.IN_TYPE -> typeStr += char
            }
        }

        return MemoryAccessor(
            getterName,
            setterName,
            addressStr.toUInt(),
            StorableType.valueOf(typeStr)
        )
    }

    override fun valueType() = MemoryAccessor::class.java
    override fun valuePattern() = "getter:setter:address:type"

    private enum class State {
        IN_ADDRESS,
        IN_GETTER,
        IN_SETTER,
        IN_TYPE
    }
}
