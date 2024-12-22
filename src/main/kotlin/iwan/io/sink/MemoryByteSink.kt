package dev.fir3.iwan.io.sink

internal interface MemoryByteSink : ByteSink {
    val buffer: ByteArray
}
