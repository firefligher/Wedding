package dev.fir3.wedding.linking.model.global

import dev.fir3.wedding.common.model.LinkerGlobal
import dev.fir3.wedding.linking.model.Relocated

internal sealed interface RelocatedGlobal : LinkerGlobal, Relocated
