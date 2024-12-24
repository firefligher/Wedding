package dev.fir3.wedding.linking

import java.nio.file.Path

internal abstract class AbstractExecutor {
    private val inputModulePaths = mutableMapOf<String, Path>()
    private var outputModulePath: Path? = null

    fun addInputModulePath(name: String, path: Path): Boolean =
        inputModulePaths.put(name, path) == null

    fun setOutputModulePath(path: Path): Path? {
        val previousPath = outputModulePath
        outputModulePath = path
        return previousPath
    }

    fun execute() {
        val inputModulePaths = inputModulePaths
            .entries
            .map { (name, path) -> Pair(name, path) }

        execute(inputModulePaths, outputModulePath)
    }

    protected abstract fun execute(
        inputModulePaths: Collection<Pair<String, Path>>,
        outputModulePath: Path?
    )
}
