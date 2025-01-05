package dev.fir3.wedding.io.foundation

class IOException : Exception {
    constructor() : super()
    constructor(cause: Throwable) : super(cause)
    constructor(message: String) : super(message)
}
