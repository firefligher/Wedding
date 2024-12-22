package dev.fir3.wedding

import dev.fir3.iwan.io.source.InputStreamByteSource
import dev.fir3.iwan.io.wasm.BinaryFormat
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

fun main() {
    Files.newInputStream(
        Paths.get("input/Hacl_Hash_SHA2.wasm"),
        StandardOpenOption.READ
    ).use { inputStream ->
        val source = InputStreamByteSource(inputStream)

        println(BinaryFormat.deserializeModule(source))
    }
}
