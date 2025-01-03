package dev.fir3.wedding.common.model

/**
 * Some information of a WebAssembly module that is associated with an index.
 */
internal sealed interface Indexed {
    /**
     * The index of this information.
     */
    val index: UInt

    /**
     * The name of the corresponding WebAssembly module.
     */
    val module: String
}
