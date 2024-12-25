package dev.fir3.wedding.linking

import dev.fir3.wedding.input.model.RenameEntry
import java.nio.file.Path

internal abstract class AbstractExecutor {
    private val inputModulePaths = mutableMapOf<String, Path>()
    private var outputModulePath: Path? = null
    private val renameEntries = mutableSetOf<RenameEntry<*>>()

    fun addInputModulePath(name: String, path: Path): Boolean =
        inputModulePaths.put(name, path) == null

    fun setOutputModulePath(path: Path): Path? {
        val previousPath = outputModulePath
        outputModulePath = path
        return previousPath
    }

    fun addRenameEntry(entry: RenameEntry<*>): Boolean =
        renameEntries.add(entry)

    fun execute() {
        val inputModulePaths = inputModulePaths
            .entries
            .map { (name, path) -> Pair(name, path) }

        execute(inputModulePaths, outputModulePath, renameEntries.toSet())
    }

    protected abstract fun execute(
        inputModulePaths: Collection<Pair<String, Path>>,
        outputModulePath: Path?,
        renameEntries: Collection<RenameEntry<*>>
    )
}
