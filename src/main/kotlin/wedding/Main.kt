package dev.fir3.wedding

import dev.fir3.iwan.io.sink.OutputStreamByteSink
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
        val module = BinaryFormat.deserializeModule(source)

        //println(module)

        Files.newOutputStream(
            Paths.get("output.wasm"),
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING
        ).use { outputStream ->
            val sink = OutputStreamByteSink(outputStream)
            BinaryFormat.serializeModule(sink, module)
        }
    }
}
