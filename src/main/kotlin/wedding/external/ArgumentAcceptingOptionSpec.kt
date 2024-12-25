package dev.fir3.wedding.external

import dev.fir3.wedding.input.IdentifierParser
import dev.fir3.wedding.input.model.RenameEntry
import joptsimple.ArgumentAcceptingOptionSpec
import joptsimple.ValueConverter
import java.nio.file.Path
import java.nio.file.Paths

internal fun ArgumentAcceptingOptionSpec<*>.withPathConverter() =
    withValuesConvertedBy(PathConverter)

internal fun ArgumentAcceptingOptionSpec<*>.withRenameConverter() =
    withValuesConvertedBy(RenameConverter)


private object PathConverter : ValueConverter<Path> {
    override fun convert(p0: String?): Path = Paths.get(requireNotNull(p0))
    override fun valueType() = Path::class.java
    override fun valuePattern(): String? = null // TODO
}

private object RenameConverter : ValueConverter<RenameEntry<*>> {
    override fun convert(p0: String?): RenameEntry<*> {
        val components = requireNotNull(p0).split(":")
        check(components.size == 2)

        val original = IdentifierParser.parse(components[0])
        val new = IdentifierParser.parse(components[1])

        return RenameEntry(
            originalIdentifier = original,
            newIdentifier = new
        )
    }

    override fun valueType() = RenameEntry::class.java
    override fun valuePattern(): String? = null // TODO
}
