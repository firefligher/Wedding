package dev.fir3.wedding.relocation

internal class RelocationTable {
    private val relocations = mutableMapOf<String, MutableMap<UInt, UInt>>()

    fun addEntry(moduleName: String, originalIndex: UInt, newIndex: UInt) {
        relocations.computeIfAbsent(moduleName) {
            mutableMapOf()
        }[originalIndex] = newIndex
    }
}
