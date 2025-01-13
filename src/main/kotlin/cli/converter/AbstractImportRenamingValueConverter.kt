package dev.fir3.wedding.cli.converter

import dev.fir3.wedding.linker.pool.ImportIdentifier
import joptsimple.ValueConverter

abstract class AbstractImportRenamingValueConverter<TResult> :
    ValueConverter<TResult> {
    final override fun convert(p0: String): TResult {
        var module = ""
        var sourceModule = ""
        var name = ""
        var newName = ""
        var escaped = false
        var state = State.IN_MODULE

        for (char in p0) {
            if (char == '\\' && !escaped) {
                escaped = true
                continue
            }

            if (char == ':' && !escaped && state == State.IN_MODULE) {
                state = State.IN_SOURCE_MODULE
                continue
            }

            if (char == '.' && !escaped && state == State.IN_SOURCE_MODULE) {
                state = State.IN_NAME
                continue
            }

            if (char == '=' && !escaped && state == State.IN_NAME) {
                state = State.IN_NEW_NAME
                continue
            }

            when (state) {
                State.IN_MODULE -> module += char
                State.IN_NAME -> name += char
                State.IN_NEW_NAME -> newName += char
                State.IN_SOURCE_MODULE -> sourceModule += char
            }

            escaped = false
        }

        return convert(
            ImportIdentifier(module, name, sourceModule),
            newName
        )
    }

    abstract fun convert(identifier: ImportIdentifier, name: String): TResult

    private enum class State {
        IN_MODULE,
        IN_SOURCE_MODULE,
        IN_NAME,
        IN_NEW_NAME
    }
}
