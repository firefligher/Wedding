package dev.fir3.wedding

import dev.fir3.iwan.io.sink.OutputStreamByteSink
import dev.fir3.iwan.io.source.InputStreamByteSource
import dev.fir3.iwan.io.wasm.BinaryFormat
import dev.fir3.wedding.linker.Linker
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

private fun loadModule(path: String) = Files.newInputStream(
    Paths.get(path),
    StandardOpenOption.READ
).use { stream ->
    BinaryFormat.deserializeModule(InputStreamByteSource(stream))
}

fun main() {
    val fstar = NamedModule("FStar", loadModule("input/FStar.wasm"))
    val hacl = NamedModule(
        "Hacl_Hash_SHA2",
        loadModule("input/Hacl_Hash_SHA2.wasm")
    )

    val support = NamedModule(
        "WasmSupport",
        loadModule("input/WasmSupport.wasm")
    )

    val linkedModule = Linker.link(fstar, hacl, support)

    Files.newOutputStream(
        Paths.get("LINKED.wasm"),
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.WRITE,
        StandardOpenOption.CREATE
    ).use { stream ->
        BinaryFormat.serializeModule(
            OutputStreamByteSink(stream),
            linkedModule
        )
    }
}
