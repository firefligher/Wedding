package dev.fir3.wedding

import dev.fir3.wedding.external.ofType
import dev.fir3.wedding.linking.Executor
import joptsimple.OptionParser
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) {
    val parser = OptionParser()

    // Initialize the CLI parser

    val optHelp = parser.acceptsAll(listOf("h", "help"), "Shows this help.")
    val optOutputPath = parser
        .acceptsAll(
            listOf("o", "output"),
            "The path of the output binary."
        )
        .withRequiredArg()
        .ofType<Path>()
        .defaultsTo(Paths.get("LINKED.wasm"))

    val optSourceNames = parser
        .acceptsAll(
            listOf("n", "name"),
            "The name of some WebAssembly module that shall be linked."
        )
        .withRequiredArg()
        .ofType<String>()

    val optSourcePaths = parser
        .acceptsAll(
            listOf("i", "input"),
            "The path to some WebAssembly module binary that shall be linked."
        )
        .withRequiredArg()
        .ofType<Path>()

    // Parse the command-line arguments

    val options = parser.parse(*args)

    if (options.has(optHelp)) {
        parser.printHelpOn(System.out)
        return
    }

    val outputPath = optOutputPath.value(options)
    val sourceNames = optSourceNames.values(options)
    val sourcePaths = optSourcePaths.values(options)

    // Build and run the executor.

    val executor = Executor()
    check(executor.setOutputModulePath(outputPath) == null)

    for ((name, path) in sourceNames.zip(sourcePaths)) {
        val isDuplicate = !executor.addInputModulePath(name, path)
        if (isDuplicate) TODO("We need some logger or something.")
    }

    executor.execute()
}
