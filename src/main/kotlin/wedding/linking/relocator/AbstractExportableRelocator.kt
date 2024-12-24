package dev.fir3.wedding.linking.relocator

import dev.fir3.wedding.Log
import dev.fir3.wedding.common.model.Exportable
import dev.fir3.wedding.linking.model.MutableRelocationTable
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.safeCast

internal abstract class AbstractExportableRelocator<
        TInputElement : Exportable,
        TInputDefinition : TInputElement,
        TInputImport : TInputElement,
        TOutputElement : Exportable,
>(
    private val inputDefinitionClass: KClass<TInputDefinition>,
    private val inputImportClass: KClass<TInputImport>
) : IndexedRelocator<TInputElement, TOutputElement> {
    override fun relocate(
        relocations: MutableRelocationTable,
        input: Iterable<TInputElement>,
        output: MutableSet<TOutputElement>,
        outputModuleName: String
    ) {
        val definitionFixups = mutableMapOf<TInputDefinition, Fixup>()
        val importFixups = mutableSetOf<Fixable<TInputImport>>()
        val imports = mutableSetOf<TInputImport>()
        var nextRelocatedIndex = 0u

        // Separate the input into exportFixups, fixables, and imports

        for (inputElement in input) {
            val inputImportElement = inputImportClass
                .safeCast(inputElement)

            if (inputImportElement != null) {
                imports.add(inputImportElement)
                continue
            }

            val inputDefinition = inputDefinitionClass
                .cast(inputElement)

            definitionFixups[inputDefinition] = Fixup()
        }

        // Linking imports: Either keep them and assign them a new index or
        // link them to some definition via the corresponding fixup.

        for (import in imports) {
            val fixups = definitionFixups.filterKeys { definition ->
                isLinkable(import, definition)
            }.entries

            check(fixups.size < 2)

            if (fixups.isNotEmpty()) {
                val (definition, fixup) = fixups.single()

                Log.d("Linking '%s' with '%s'", import, definition)

                importFixups += Fixable(fixup, import)
                continue
            }

            Log.d("Preserving '%s'", import)

            val relocatedIndex = nextRelocatedIndex++
            output += deriveOutputElement(
                import,
                outputModuleName,
                relocatedIndex
            )

            relocations[import.module, import.index] = relocatedIndex
        }

        // Assigning indices to the definitions and deriving the corresponding
        // output elements

        for ((definition, fixup) in definitionFixups) {
            val relocatedIndex = fixup.index ?: fixup.let {
                val assignedIndex = nextRelocatedIndex++
                fixup.index = assignedIndex
                assignedIndex
            }

            output += deriveOutputElement(
                definition,
                outputModuleName,
                relocatedIndex
            )

            relocations[definition.module, definition.index] = relocatedIndex
        }

        // Adding the relocation entries for the linked imports.

        for ((fixup, unfixed) in importFixups) {
            relocations[unfixed.module, unfixed.index] =
                checkNotNull(fixup.index)
        }
    }

    protected abstract fun deriveOutputElement(
        input: TInputElement,
        outputModuleName: String,
        relocatedIndex: UInt
    ): TOutputElement

    protected abstract fun isLinkable(
        import: TInputImport,
        definition: TInputDefinition
    ): Boolean
}

private data class Fixable<TUnfixed>(
    val fixup: Fixup,
    val unfixed: TUnfixed
)

private data class Fixup(var index: UInt? = null)
