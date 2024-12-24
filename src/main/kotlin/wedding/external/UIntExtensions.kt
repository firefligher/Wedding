package dev.fir3.wedding.external

internal fun UInt.toIntOrThrow(): Int {
    check(this <= Int.MAX_VALUE.toUInt())
    return this.toInt()
}
