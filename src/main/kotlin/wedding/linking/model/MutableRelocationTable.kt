package dev.fir3.wedding.linking.model

internal data class MutableRelocationTable(
    private val relocations: MutableMap<String, MutableMap<UInt, UInt>> =
        mutableMapOf()
): RelocationTable {
    override operator fun get(module: String, originalIndex: UInt) =
        relocations[module]?.get(originalIndex)

    operator fun set(module: String, originalIndex: UInt, newIndex: UInt) {
        val previousIndex = relocations.computeIfAbsent(module) {
            mutableMapOf()
        }.put(originalIndex, newIndex)

        check(previousIndex == null)
    }
}
