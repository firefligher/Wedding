package dev.fir3.wedding.external

import joptsimple.ArgumentAcceptingOptionSpec
import joptsimple.ValueConverter
import java.nio.file.Path
import java.nio.file.Paths

internal fun ArgumentAcceptingOptionSpec<*>.withPathConverter() =
    withValuesConvertedBy(PathConverter)

private object PathConverter : ValueConverter<Path> {
    override fun convert(p0: String?): Path = Paths.get(requireNotNull(p0))
    override fun valueType(): Class<out Path> = Path::class.java
    override fun valuePattern(): String? = null
}
