package dev.fir3.wedding.linking.renamer

import dev.fir3.wedding.Log
import dev.fir3.wedding.input.IdentifierParser
import dev.fir3.wedding.input.model.MutableInputContainer
import dev.fir3.wedding.input.model.RenameEntry
import dev.fir3.wedding.input.model.identifier.Identifier
import kotlin.reflect.KClass

internal abstract class AbstractRenamer<
        TIdentifier : Identifier<*>,
        TObject : Any
>(
    private val objectClass: KClass<TObject>
) {
    fun rename(
        container: MutableInputContainer,
        renameEntries: Collection<RenameEntry<TIdentifier>>
    ) {
        val objects = resolveObjectSet(container)
        val identifiableObjects = objects
            .filterIsInstance(objectClass.java)
            .mapNotNull { obj ->
            resolveIdentifier(obj)?.let { identifier ->
                Pair(identifier, obj)
            }
        }.toMap().toMutableMap()

        for (renameEntry in renameEntries) {
            val obj = identifiableObjects[renameEntry.originalIdentifier]

            if (obj == null) {
                Log.i(
                    "Cannot rename '%s' to '%s' because object does not " +
                            "exist.",
                    IdentifierParser.stringify(renameEntry.originalIdentifier),
                    IdentifierParser.stringify(renameEntry.newIdentifier)
                )

                continue
            }

            objects.remove(obj)
            identifiableObjects.remove(renameEntry.originalIdentifier)

            val newObj = rename(obj, renameEntry.newIdentifier)
            objects += newObj
            identifiableObjects[renameEntry.newIdentifier] = newObj
        }
    }

    protected abstract fun rename(
        obj: TObject,
        newIdentifier: TIdentifier
    ): TObject

    protected abstract fun resolveIdentifier(obj: TObject): TIdentifier?
    protected abstract fun resolveObjectSet(
        container: MutableInputContainer
    ): MutableSet<in TObject>
}
