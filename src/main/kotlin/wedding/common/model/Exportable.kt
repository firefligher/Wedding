package dev.fir3.wedding.common.model

/**
 * Some information that WebAssembly modules can export.
 */
internal interface Exportable : Indexed {
    /**
     * If not `null`, this information is exported by the corresponding
     * WebAssembly module with the specified name.
     */
    val exportName: String?
}
