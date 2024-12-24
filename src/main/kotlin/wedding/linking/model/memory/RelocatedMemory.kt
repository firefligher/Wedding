package dev.fir3.wedding.linking.model.memory

import dev.fir3.wedding.common.model.LinkerMemory
import dev.fir3.wedding.linking.model.Relocated

internal sealed interface RelocatedMemory : LinkerMemory, Relocated
