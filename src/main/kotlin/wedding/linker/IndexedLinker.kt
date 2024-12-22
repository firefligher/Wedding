package dev.fir3.wedding.linker

import dev.fir3.wedding.IndexedObject
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

internal class IndexedLinker<
        TIndexed,
        TIndexedExport,
        TIndexedImport,
        TIndexOutput
>(
    private val indexedExportClass: KClass<TIndexedExport>,
    private val indexedImportClass: KClass<TIndexedImport>,
    private val matcher: (TIndexedImport, TIndexedExport) -> Boolean,
    private val adjuster: (TIndexed, UInt, String) -> TIndexOutput,
    private val importMatcher: (TIndexOutput, TIndexedImport) -> Boolean
) where TIndexed : IndexedObject,
        TIndexedImport : TIndexed,
        TIndexedExport : TIndexed,
        TIndexOutput : IndexedObject {
    fun link(
        relocations: RelocationTable,
        input: Set<TIndexed>,
        output: MutableSet<TIndexOutput>,
        outputModuleName: String
    ) {
        val imports = mutableListOf<TIndexedImport>()
        val fixups = mutableMapOf<TIndexed, Fixup>()

        for (indexed in input) {
            val import = indexedImportClass.safeCast(indexed)

            if (import == null) fixups[indexed] = Fixup()
            else imports += import
        }

        var nextRelocatedIndex = 0u

        while (imports.isNotEmpty()) {
            val import = imports.removeFirst()
            val fixup = fixups.entries.singleOrNull { (entry, _) ->
                indexedExportClass.safeCast(entry)?.let { export ->
                    matcher(import, export)
                } ?: false
            }?.value

            if (fixup != null) {
                fixups[import] = fixup
                continue
            }

            val duplicate = output
                .singleOrNull { importMatcher(it, import) }

            if (duplicate != null) {
                relocations.addEntry(
                    import.moduleName,
                    import.index,
                    duplicate.index
                )
                continue
            }

            val relocatedIndex = nextRelocatedIndex++
            output += adjuster(import, relocatedIndex, outputModuleName)
            relocations.addEntry(
                import.moduleName,
                import.index,
                relocatedIndex
            )
        }

        for ((indexed, fixup) in fixups) {
            val relocatedIndex = fixup.index ?: fixup.let {
                val assignedIndex = nextRelocatedIndex++
                it.index = assignedIndex
                assignedIndex
            }

            if (!indexedImportClass.isInstance(indexed))
                output += adjuster(indexed, relocatedIndex, outputModuleName)

            relocations.addEntry(
                indexed.moduleName,
                indexed.index,
                relocatedIndex
            )
        }
    }
}
