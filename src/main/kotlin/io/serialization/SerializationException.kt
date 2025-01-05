package dev.fir3.wedding.io.serialization

class SerializationException : Exception {
    constructor(cause: Throwable) : super(cause)
    constructor(message: String) : super(message)
}
