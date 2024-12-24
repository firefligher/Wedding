package dev.fir3.wedding.external

import dev.fir3.iwan.io.wasm.models.Export

internal inline fun <TExport : Export> List<TExport>.resolveName(
    index: UInt,
    indexFunction: (TExport) -> UInt
) = filter { export -> indexFunction(export) == index }
    .map(Export::name)
    .singleOrNull()
