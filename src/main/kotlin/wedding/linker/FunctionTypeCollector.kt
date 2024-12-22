package dev.fir3.wedding.linker

import dev.fir3.wedding.linker.function.LinkedFunctionEntry

internal fun collectFunctionTypes(
    functions: Collection<LinkedFunctionEntry>
) = functions.map { f -> f.type }.distinct()
