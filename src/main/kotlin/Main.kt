package dev.fir3.wedding

import dev.fir3.wedding.cli.Out
import dev.fir3.wedding.cli.converter.*
import dev.fir3.wedding.io.foundation.InputStreamByteSource
import dev.fir3.wedding.io.foundation.OutputStreamByteSink
import dev.fir3.wedding.io.wasm.WasmContext
import dev.fir3.wedding.linker.fixing.fixDatas
import dev.fir3.wedding.linker.fixing.fixElements
import dev.fir3.wedding.linker.fixing.fixFunctions
import dev.fir3.wedding.linker.fixing.fixGlobals
import dev.fir3.wedding.linker.linking.link
import dev.fir3.wedding.linker.linking.linkStartFunctions
import dev.fir3.wedding.linker.merging.*
import dev.fir3.wedding.linker.pool.*
import dev.fir3.wedding.linker.relocation.*
import dev.fir3.wedding.linker.renaming.rename
import dev.fir3.wedding.linker.renaming.renameImport
import dev.fir3.wedding.linker.renaming.renameImportModule
import dev.fir3.wedding.linker.sanity.checkForDuplicateExports
import dev.fir3.wedding.linker.synthetic.addData
import dev.fir3.wedding.linker.synthetic.addGetter
import dev.fir3.wedding.linker.synthetic.addGlobal
import dev.fir3.wedding.linker.synthetic.addSetter
import dev.fir3.wedding.wasm.Module
import joptsimple.OptionParser
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.system.exitProcess

private fun readModule(path: String): Module = FileInputStream(path)
    .let(::InputStreamByteSource)
    .use(WasmContext::deserialize)

private fun writeModule(path: String, module: Module) = FileOutputStream(path)
    .let(::OutputStreamByteSink)
    .use { sink ->
        WasmContext.serialize(sink, module)
    }

fun main(args: Array<String>) {
    val parser = OptionParser()

    // Initialize the CLI Parser

    val optAddData = parser
        .acceptsAll(
            listOf("add-data"),
            "Adds active data section to the resulting module."
        )
        .withRequiredArg()
        .withValuesConvertedBy(DataDefinitionConverter)

    val optAddGlobal = parser
        .acceptsAll(
            listOf("add-global"),
            "Adds a global to the resulting module."
        )
        .withRequiredArg()
        .withValuesConvertedBy(GlobalDefinitionConverter)

    val optAddMemoryAccessors = parser
        .acceptsAll(
            listOf("add-memory-accessors"),
            "Adds a pair of memory accessor functions to the resulting module."
        )
        .withRequiredArg()
        .withValuesConvertedBy(MemoryAccessorConverter)

    val optDisplayPool = parser
        .acceptsAll(
            listOf("display-pool"),
            "Displays the pool after parsing all input files."
        )

    val optDisplayUnresolved = parser
        .acceptsAll(
            listOf("display-unresolved"),
            "Displays unresolved imports that linking could not resolve."
        )

    val optHelp = parser.acceptsAll(listOf("h", "help"), "Shows this help.")
    val optOutputPath = parser
        .acceptsAll(
            listOf("o", "output"),
            "The path to the resulting WebAssembly module binary."
        )
        .withRequiredArg()

    val optRename = parser
        .acceptsAll(
            listOf("r", "rename"),
            "Replaces all names of an export."
        )
        .withRequiredArg()
        .withValuesConvertedBy(RenamingConverter)

    val optRenameImport = parser
        .acceptsAll(
            listOf("ri", "rename-import"),
            "Renames an import."
        )
        .withRequiredArg()
        .withValuesConvertedBy(ImportRenamingConverter)

    val optRenameImportModule = parser
        .acceptsAll(
            listOf("rim", "rename-import-module"),
            "Renames the source module of some import."
        )
        .withRequiredArg()
        .withValuesConvertedBy(ImportModuleRenamingConverter)

    val optSourceModules = parser
        .acceptsAll(
            listOf("m", "module"),
            "The name of some WebAssembly module that shall be linked."
        )
        .withRequiredArg()

    val optSourcePaths = parser
        .acceptsAll(
            listOf("i", "input"),
            "The path to some WebAssembly module binary that shall be linked."
        )
        .withRequiredArg()

    // Parse the command-line arguments

    val options = parser.parse(*args)

    if (options.has(optHelp)) {
        parser.printHelpOn(System.out)
        return
    }

    // Evaluate the command-line options.

    val sourceModules = optSourceModules.values(options)
    val sourcePaths = optSourcePaths.values(options)

    if (sourceModules.size != sourcePaths.size) {
        Out.writeError(
            "The number of source modules does not match the number paths. " +
                    "Received %d modules and %d paths. Please ensure that " +
                    "each module corresponds a path and vice versa.",
            sourceModules.size,
            sourcePaths.size
        )

        exitProcess(1)
    }

    val pool = Pool()

    sourceModules.zip(sourcePaths).forEach { (name, path) ->
        Out.writeInfo("Loading module '%s' from '%s' into pool.", name, path)
        pool.add(name, readModule(path))
    }

    // Generate memory accessors.

    val memoryAccessors = optAddMemoryAccessors.values(options)

    for ((getter, setter, address, type) in memoryAccessors) {
        val getterFunction = pool.addGetter(getter, address, type)
        val setterFunction = pool.addSetter(setter, address, type)

        Out.writeInfo(
            "Created getter '%s' for '%s' at '%s'.",
            getterFunction.identifier,
            type,
            address.toString()
        )

        Out.writeInfo(
            "Created setter '%s' for '%s' at '%s'.",
            setterFunction.identifier,
            type,
            address.toString()
        )
    }

    // Generate globals.

    val globals = optAddGlobal.values(options)

    for ((initialValue, isMutable, name, type) in globals) {
        val global = pool.addGlobal(name, isMutable, type, initialValue)
        Out.writeInfo("Created global '%s'.", global.identifier)
    }

    // Generate data.

    val datas = optAddData.values(options)

    for ((identifier, address, bytes) in datas) {
        val data = pool.addData(identifier, address, bytes)
        Out.writeInfo("Created data '%s'.", data.identifier)
    }

    // Print some statistics and information.

    Out.writeInfo(
        "Pool statistics: %d datas, %d elements, %d functions, " +
                "%d function types, %d globals, %d memories, %d tables.",
        pool.datas.size,
        pool.elements.size,
        pool.functions.size,
        pool.functionTypes.size,
        pool.globals.size,
        pool.memories.size,
        pool.tables.size
    )

    if (options.has(optDisplayPool)) {
        Out.writeInfo(
            "Objects with the following identifiers exist in the pool:"
        )

        pool.map(Object::identifier)
            .map(Identifier::toString)
            .forEach(Out::writeInfo)
    }

    // The actual linking

    val renamings = optRename.values(options)
    val importRenamings = optRenameImport.values(options)
    val importModuleRenamings = optRenameImportModule.values(options)

    for ((identifier, newName) in renamings) {
        if (pool.rename(identifier, newName)) {
            Out.writeInfo(
                "Successfully renamed '%s' to '%s'",
                identifier,
                ExportIdentifier(identifier.module, setOf(newName))
            )
        } else {
            Out.writeError(
                "Failed renaming '%s', because it does not exist.",
                identifier
            )
        }
    }

    for ((identifier, newModule) in importModuleRenamings) {
        if (pool.renameImportModule(identifier, newModule)) {
            Out.writeInfo(
                "Successfully renamed '%s' to '%s'",
                identifier,
                identifier.copy(sourceModule = newModule)
            )
        } else {
            Out.writeError(
                "Failed renaming '%s', because it does not exist.",
                identifier
            )
        }
    }

    for ((identifier, newName) in importRenamings) {
        if (pool.renameImport(identifier, newName)) {
            Out.writeInfo(
                "Successfully renamed '%s' to '%s'",
                identifier,
                identifier.copy(name = newName)
            )
        } else {
            Out.writeError(
                "Failed renaming '%s', because it does not exist.",
                identifier
            )
        }
    }

    val duplicateExports = pool.checkForDuplicateExports()

    if (duplicateExports.isNotEmpty()) {
        Out.writeInfo(
            "Linking failed due to duplicates in the exported names."
        )

        for (export in duplicateExports) {
            Out.writeInfo(
                "The name '%s' collides for objects with the following " +
                        "identifiers:",
                export.name
            )

            for (`object` in export.objects) {
                Out.writeInfo(" * %s", `object`.identifier)
            }
        }

        exitProcess(1)
    }

    pool.linkStartFunctions(UUID.randomUUID().toString())
    val conflicts = pool.link()

    if (conflicts.isNotEmpty()) {
        // TODO
        println(conflicts)
        exitProcess(1)
    }

    if (options.has(optDisplayUnresolved)) {
        val unresolvedObjects = pool.unresolved()

        if (unresolvedObjects.isEmpty()) {
            Out.writeInfo("All objects are resolved.")
        } else {
            Out.writeInfo(
                "The objects with the following identifiers are unresolved:"
            )

            for (unresolvedObject in unresolvedObjects) {
                Out.writeInfo(" * %s", unresolvedObject.identifier)
            }
        }
    }

    pool.mergeFunctionImports()
    pool.mergeFunctionTypes()
    pool.mergeGlobalImports()
    pool.mergeMemoryImports()
    pool.mergeTableImports()

    pool.relocateData()
    pool.relocateElements()
    pool.relocateFunctionTypes()
    pool.relocateFunctions()
    pool.relocateGlobals()
    pool.relocateMemories()
    pool.relocateTables()

    val sourceIndex = pool.createSourceIndex()
    pool.fixDatas(sourceIndex)
    pool.fixElements(sourceIndex)
    pool.fixFunctions(sourceIndex)
    pool.fixGlobals(sourceIndex)

    if (!options.has(optOutputPath)) {
        Out.writeWarning(
            "Cannot persist module, because no output path was specified."
        )

        return
    }

    val outputPath = optOutputPath.value(options)

    Out.writeInfo("Writing linked module to '%s'.", outputPath)
    writeModule(outputPath, pool.toModule())
}
