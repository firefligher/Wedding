package dev.fir3.wedding.cli

object Out {
    fun writeError(format: String, vararg parameters: Any) {
        System.err.printf("[ERR] %s\n", format.format(*parameters))
    }

    fun writeInfo(format: String, vararg parameters: Any) {
        System.out.printf("[INF] %s\n", format.format(*parameters))
    }

    fun writeWarning(format: String, vararg parameters: Any) {
        System.out.printf("[WRN] %s\n", format.format(*parameters))
    }
}
