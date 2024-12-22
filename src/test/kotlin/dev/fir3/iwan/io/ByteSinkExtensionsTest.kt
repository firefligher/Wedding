package dev.fir3.iwan.io

import dev.fir3.iwan.io.sink.createMemoryByteSink
import dev.fir3.iwan.io.sink.writeVarInt32
import kotlin.test.Test

internal class ByteSinkExtensionsTest {
    @Test
    fun testVarInt32() {
        val sink = createMemoryByteSink()
        sink.writeVarInt32(64)
        println(sink.buffer.toUByteArray().contentToString())
    }
}
