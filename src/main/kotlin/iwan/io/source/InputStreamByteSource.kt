package dev.fir3.iwan.io.source

import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.min

class InputStreamByteSource(
    source: InputStream
) : ByteSource {
    private var _buffer: ByteArray? = null
    private var _bufferPosition = 0
    private var _bufferLimit = 0
    private var _isEof: Boolean? = null
    private var _marked = false
    private val _source = BufferedInputStream(source)

    override fun isEof(): Boolean {
        var safeIsEof = _isEof

        if (safeIsEof == null) {
            _source.mark(1)
            safeIsEof = (_source.read() == -1)
            _isEof = safeIsEof
            _source.reset()
        }

        return safeIsEof
    }

    override fun mark() {
        val safeBuffer = _buffer
        _marked = true

        if (safeBuffer == null || _bufferPosition == _bufferLimit) {
            _buffer = ByteArray(1)
            _bufferPosition = 0
            _bufferLimit = 0
            return
        }

        _buffer = safeBuffer.copyOfRange(_bufferPosition, _bufferLimit)
        _bufferLimit -= _bufferPosition
        _bufferPosition = 0
    }

    @Throws(IOException::class)
    override fun read(dst: ByteArray, offset: Int, count: Int) {
        var safeBuffer = _buffer

        if (_marked) {
            // If required, resize buffer for the incoming bytes.

            checkNotNull(safeBuffer) {
                "Mark is set, but internal buffer is absent"
            }

            val expectedNewLimit = _bufferPosition + count
            var requiredBufferSize = safeBuffer.size

            while (requiredBufferSize < expectedNewLimit) {
                requiredBufferSize *= 2
            }

            if (safeBuffer.size < requiredBufferSize) {
                safeBuffer = safeBuffer.copyOf(expectedNewLimit)
                _buffer = safeBuffer
            }

            // Attempt to read the requested bytes into the internal buffer.

            var remainingReadCount = count + _bufferPosition - _bufferLimit

            while (remainingReadCount > 0) {
                val readBytes = _source
                    .read(safeBuffer, _bufferLimit, remainingReadCount)

                if (readBytes == -1) {
                    break
                }

                _bufferLimit += readBytes
                remainingReadCount -= readBytes
            }
        }

        // Read as much data from the internal buffer as possible, fill the
        // remaining gap with bytes from the stream afterward.

        var copiedBytes: Int

        if (safeBuffer != null) {
            copiedBytes = min(_bufferLimit - _bufferPosition, count)
            safeBuffer.copyInto(
                dst,
                offset,
                _bufferPosition,
                _bufferPosition + copiedBytes
            )

            _bufferPosition += copiedBytes
        } else {
            copiedBytes = 0
        }

        while (copiedBytes < count) {
            val readBytes = _source
                .read(dst, offset + copiedBytes, count - copiedBytes)

            if (readBytes == -1) {
                _isEof = true
                throw IOException("Reached end of source stream")
            }

            copiedBytes += readBytes
            _isEof = null
        }

        // If there is no mark, but the internal buffer exists and the position
        // is equal to the limit, then we can deallocate the buffer.

        val deallocateBuffer = !_marked
                && safeBuffer != null
                && _bufferPosition == _bufferLimit

        if (deallocateBuffer) {
            safeBuffer = null
            _buffer = safeBuffer
        }
    }

    override fun reset(unmark: Boolean) {
        check(_marked) { "No mark in place." }

        _bufferPosition = 0
        _marked = !unmark

        if (_bufferLimit > 0) {
            _isEof = false
        }
    }

    override fun unmark() {
        _marked = false
    }
}
