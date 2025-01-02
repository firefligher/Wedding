package dev.fir3.wedding

import dev.fir3.wedding.external.withIdentifierConverter
import dev.fir3.wedding.external.withPathConverter
import dev.fir3.wedding.external.withRenameConverter
import dev.fir3.wedding.input.model.identifier.ExportedGlobalIdentifier
import dev.fir3.wedding.input.model.identifier.GlobalIdentifier
import dev.fir3.wedding.linking.IdentifierPrintingExecutor
import dev.fir3.wedding.linking.LinkingExecutor
import joptsimple.OptionParser
import java.nio.file.Paths

fun main(args: Array<String>) {
    val parser = OptionParser()

    // Initialize the CLI parser

    val optDefineGlobal = parser
        .accepts(
            "define-global",
            "Defines an exported global and corresponding accessors."
        )
        .withRequiredArg()
        .withIdentifierConverter()

    val optHelp = parser.acceptsAll(listOf("h", "help"), "Shows this help.")

    val optOutputPath = parser
        .acceptsAll(
            listOf("o", "output"),
            "The path of the output binary."
        )
        .withRequiredArg()
        .withPathConverter()
        .defaultsTo(Paths.get("LINKED.wasm"))

    val optPrintIdentifiers = parser
        .accepts("print-identifiers")

    val optRename = parser
        .acceptsAll(
            listOf("r", "rename"),
            "Renames the corresponding symbol."
        )
        .withRequiredArg()
        .withRenameConverter()

    val optSourceNames = parser
        .acceptsAll(
            listOf("n", "name"),
            "The name of some WebAssembly module that shall be linked."
        )
        .withRequiredArg()

    val optSourcePaths = parser
        .acceptsAll(
            listOf("i", "input"),
            "The path to some WebAssembly module binary that shall be linked."
        )
        .withRequiredArg()
        .withPathConverter()

    val optWrapGlobal = parser
        .accepts(
            "wrap-global",
            "Defines accessors for some exported or imported global."
        )
        .withRequiredArg()
        .withIdentifierConverter()

    // Parse the command-line arguments

    val options = parser.parse(*args)

    if (options.has(optHelp)) {
        parser.printHelpOn(System.out)
        return
    }

    val definedGlobals = optDefineGlobal.values(options)
    val outputPath = optOutputPath.value(options)
    val renamings = optRename.values(options)
    val sourceNames = optSourceNames.values(options)
    val sourcePaths = optSourcePaths.values(options)
    val wrappedGlobals = optWrapGlobal.values(options)

    // Build and run the executor.

    val executor = if (options.has(optPrintIdentifiers)) {
        IdentifierPrintingExecutor()
    } else {
        LinkingExecutor()
    }

    check(executor.setOutputModulePath(outputPath) == null)

    for ((name, path) in sourceNames.zip(sourcePaths)) {
        val isDuplicate = !executor.addInputModulePath(name, path)
        if (isDuplicate) TODO("We need some logger or something.")
    }

    for (renaming in renamings) {
        val isDuplicate = !executor.addRenameEntry(renaming)
        if (isDuplicate) TODO()
    }

    definedGlobals.forEach {
        executor.defineGlobal(it as ExportedGlobalIdentifier)
    }

    wrappedGlobals.forEach {
        executor.wrapGlobal(it as GlobalIdentifier)
    }

    executor.execute()
}
