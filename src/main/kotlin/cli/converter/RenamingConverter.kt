package dev.fir3.wedding.cli.converter

import dev.fir3.wedding.cli.Renaming
import dev.fir3.wedding.linker.pool.ExportIdentifier
import joptsimple.ValueConverter

object RenamingConverter : ValueConverter<Renaming> {
    override fun convert(p0: String): Renaming {
        val names = mutableSetOf<String>()
        var currentName = ""
        var module = ""
        var newName = ""
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

            if (char == ',' && !escaped && state == State.IN_NAME) {
                names += currentName
                continue
            }

            if (char == '=' && !escaped && state == State.IN_NAME) {
                names += currentName
                state = State.IN_NEW_NAME
                continue
            }

            when (state) {
                State.IN_MODULE -> module += char
                State.IN_NAME -> currentName += char
                State.IN_NEW_NAME -> newName += char
            }

            escaped = false
        }

        return Renaming(ExportIdentifier(module, names), newName)
    }

    override fun valueType() = Renaming::class.java
    override fun valuePattern() = "module.name1,name2,...=new-name"

    private enum class State {
        IN_MODULE,
        IN_NAME,
        IN_NEW_NAME
    }
}
