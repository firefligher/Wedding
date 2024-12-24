package dev.fir3.wedding

internal object Log {
    fun d(format: String, vararg params: Any?) {
        println("[DBG] " + format.format(*params))
    }

    fun i(format: String, vararg params: Any?) {
        println("[INF] " + format.format(*params))
    }
}