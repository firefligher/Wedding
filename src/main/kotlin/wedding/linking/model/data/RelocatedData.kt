package dev.fir3.wedding.linking.model.data

import dev.fir3.wedding.common.model.LinkerData
import dev.fir3.wedding.linking.model.Relocated

internal sealed interface RelocatedData : LinkerData, Relocated
