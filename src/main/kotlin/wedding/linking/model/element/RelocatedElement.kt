package dev.fir3.wedding.linking.model.element

import dev.fir3.wedding.common.model.LinkerElement
import dev.fir3.wedding.linking.model.Relocated

internal sealed interface RelocatedElement : LinkerElement, Relocated
