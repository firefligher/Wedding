package dev.fir3.wedding.linking

import dev.fir3.wedding.input.model.RenameEntry
import dev.fir3.wedding.input.model.identifier.ExportedGlobalIdentifier
import dev.fir3.wedding.input.model.identifier.GlobalIdentifier
import dev.fir3.wedding.input.model.identifier.Identifier
import java.nio.file.Path

internal abstract class AbstractExecutor {
    private val definedGlobals = mutableSetOf<ExportedGlobalIdentifier>()
    private val inputModulePaths = mutableMapOf<String, Path>()
    private var outputModulePath: Path? = null
    private val renameEntries = mutableSetOf<RenameEntry<*>>()
    private val wrappedGlobals = mutableSetOf<GlobalIdentifier>()

    fun addInputModulePath(name: String, path: Path): Boolean =
        inputModulePaths.put(name, path) == null

    fun defineGlobal(identifier: ExportedGlobalIdentifier) = identifier
        .let(definedGlobals::add)

    fun setOutputModulePath(path: Path): Path? {
        val previousPath = outputModulePath
        outputModulePath = path
        return previousPath
    }

    fun addRenameEntry(entry: RenameEntry<*>): Boolean =
        renameEntries.add(entry)

    fun wrapGlobal(identifier: GlobalIdentifier) = identifier
        .let(wrappedGlobals::add)

    fun execute() {
        val inputModulePaths = inputModulePaths
            .entries
            .map { (name, path) -> Pair(name, path) }

        execute(
            inputModulePaths,
            outputModulePath,
            renameEntries.toSet(),
            definedGlobals,
            wrappedGlobals
        )
    }

    protected abstract fun execute(
        inputModulePaths: Collection<Pair<String, Path>>,
        outputModulePath: Path?,
        renameEntries: Collection<RenameEntry<*>>,
        definedGlobals: Collection<ExportedGlobalIdentifier>,
        wrappedGlobals: Collection<GlobalIdentifier>
    )
}
