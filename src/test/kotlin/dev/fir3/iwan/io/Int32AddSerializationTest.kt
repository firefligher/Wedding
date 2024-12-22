package dev.fir3.iwan.io

import dev.fir3.iwan.io.serialization.SerializationContextBuilder
import dev.fir3.iwan.io.sink.createMemoryByteSink
import dev.fir3.iwan.io.wasm.models.instructions.FlatInstruction
import dev.fir3.iwan.io.wasm.serialization.instructions.InstructionStrategy
import kotlin.test.Test

internal class Int32AddSerializationTest {
    @Test
    fun serialize() {
        val sink = createMemoryByteSink()
        val b = SerializationContextBuilder()

        InstructionStrategy.serialize(
            sink,
            b.build(),
            FlatInstruction.INT32_ADD
        )

        println(sink.buffer.contentToString())
    }
}
