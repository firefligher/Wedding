package dev.fir3.wedding.cli.converter

import dev.fir3.wedding.cli.DataDefinition
import dev.fir3.wedding.linker.pool.ExportIdentifier
import joptsimple.ValueConverter
import java.math.BigInteger

object DataDefinitionConverter : ValueConverter<DataDefinition> {
    override fun convert(p0: String): DataDefinition {
        var module = ""
        var name = ""
        var addressStr = ""
        var bytesStr = ""
        var escaped = false
        var state = State.IN_MODULE

        for (char in p0) {
            if (char == '\\' && !escaped) {
                escaped = true
                continue
            }

            if (char == '.' && !escaped && state == State.IN_MODULE) {
                state = State.IN_NAME
                continue
            }

            if (char == ':' && !escaped && state == State.IN_NAME) {
                state = State.IN_ADDRESS
                continue
            }

            if (char == ':' && !escaped && state == State.IN_ADDRESS) {
                state = State.IN_BYTES
                continue
            }

            when (state) {
                State.IN_MODULE -> module += char
                State.IN_NAME -> name += char
                State.IN_ADDRESS -> addressStr += char
                State.IN_BYTES -> bytesStr += char
            }

            escaped = false
        }

        return DataDefinition(
            ExportIdentifier(module, setOf(name)),
            addressStr.toUInt(),
            BigInteger(bytesStr, 16).toByteArray()
        )
    }

    override fun valueType() = DataDefinition::class.java
    override fun valuePattern() = "module.memory:address:hex-bytes"

    private enum class State {
        IN_MODULE,
        IN_NAME,
        IN_ADDRESS,
        IN_BYTES
    }
}
