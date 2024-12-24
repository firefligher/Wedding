package dev.fir3.wedding.linking.model.function

import dev.fir3.wedding.common.model.LinkerFunction
import dev.fir3.wedding.linking.model.Relocated

internal sealed interface RelocatedFunction : LinkerFunction, Relocated
