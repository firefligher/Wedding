package dev.fir3.wedding.io.foundation

class CountingByteSource(private val source: ByteSource) : ByteSource {
    var count: UInt = 0u
        private set

    override fun read(buffer: ByteArray, offset: UInt, count: UInt) {
        source.read(buffer, offset, count)
        this.count += count
    }
}
