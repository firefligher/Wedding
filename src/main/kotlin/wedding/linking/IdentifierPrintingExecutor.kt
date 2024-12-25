package dev.fir3.wedding.linking

import dev.fir3.iwan.io.source.InputStreamByteSource
import dev.fir3.iwan.io.wasm.BinaryFormat
import dev.fir3.wedding.Log
import dev.fir3.wedding.input.IdentifierParser
import dev.fir3.wedding.input.loader.DataLoader
import dev.fir3.wedding.input.loader.FunctionLoader
import dev.fir3.wedding.input.loader.GlobalLoader
import dev.fir3.wedding.input.loader.MemoryLoader
import dev.fir3.wedding.input.model.MutableInputContainer
import dev.fir3.wedding.input.model.RenameEntry
import dev.fir3.wedding.input.model.identifier.identifier
import dev.fir3.wedding.linking.model.NamedModule
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

internal class IdentifierPrintingExecutor : AbstractExecutor() {
    override fun execute(
        inputModulePaths: Collection<Pair<String, Path>>,
        outputModulePath: Path?,
        renameEntries: Collection<RenameEntry<*>>
    ) {
        // Deserialize the WebAssembly modules

        val inputModules = inputModulePaths.map { (name, path) ->
            try {
                val module = Files
                    .newInputStream(
                        path,
                        StandardOpenOption.READ
                    )
                    .let(::InputStreamByteSource)
                    .use(BinaryFormat::deserializeModule)

                NamedModule(name, module)
            } catch (ex: IOException) {
                ex.printStackTrace()
                TODO("We should implement error handling here")
            }
        }

        // Merge all significant information into a large source container,
        // that we then consider as immutable.

        val inputContainer = MutableInputContainer()

        DataLoader.load(inputModules, inputContainer.datas)
        FunctionLoader.load(inputModules, inputContainer.functions)
        GlobalLoader.load(inputModules, inputContainer.globals)
        MemoryLoader.load(inputModules, inputContainer.memories)

        for (function in inputContainer.functions) {
            function.identifier?.let { identifier ->
                Log.i("Function: %s", IdentifierParser.stringify(identifier))
            }
        }

        for (global in inputContainer.globals) {
            global.identifier?.let { identifier ->
                Log.i("Global: %s", IdentifierParser.stringify(identifier))
            }
        }

        for (memory in inputContainer.memories) {
            memory.identifier?.let { identifier ->
                Log.i("Memory: %s", IdentifierParser.stringify(identifier))
            }
        }
    }
}
