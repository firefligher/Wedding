package dev.fir3.wedding.common.model

interface Debuggable {
    val debugIdentifier: String get() = toString()
}
