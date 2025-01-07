package dev.fir3.wedding

import dev.fir3.wedding.io.foundation.InputStreamByteSource
import dev.fir3.wedding.io.foundation.OutputStreamByteSink
import dev.fir3.wedding.io.wasm.WasmContext
import dev.fir3.wedding.linker.fixing.fixDatas
import dev.fir3.wedding.linker.fixing.fixElements
import dev.fir3.wedding.linker.fixing.fixFunctions
import dev.fir3.wedding.linker.fixing.fixGlobals
import dev.fir3.wedding.linker.linking.link
import dev.fir3.wedding.linker.merging.*
import dev.fir3.wedding.linker.pool.*
import dev.fir3.wedding.linker.relocation.*
import dev.fir3.wedding.linker.renaming.rename
import dev.fir3.wedding.linker.renaming.renameImportModule
import dev.fir3.wedding.linker.sanity.checkForDuplicateExports
import dev.fir3.wedding.wasm.Module
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.system.exitProcess

private fun readModule(path: String): Module = FileInputStream(path)
    .let(::InputStreamByteSource)
    .use(WasmContext::deserialize)

private fun writeModule(path: String, module: Module) = FileOutputStream(path)
    .let(::OutputStreamByteSink)
    .use { sink ->
        WasmContext.serialize(sink, module)
    }

private fun linkModules(
    modules: Map<String, Module>,
    preprocessing: ((Pool) -> Unit)? = null
): Module {
    val pool = Pool()

    modules.forEach { (name, module) -> pool.add(name, module) }
    preprocessing?.invoke(pool)

    val duplicateExports = pool.checkForDuplicateExports()

    if (duplicateExports.isNotEmpty()) {
        println(duplicateExports)
        exitProcess(1)
    }

    val conflicts = pool.link()

    if (conflicts.isNotEmpty()) {
        println(conflicts)
        exitProcess(1)
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

    return pool.toModule()
}

fun main(args: Array<String>) {
    // Test Scenario

    val moduleFStar = readModule("input/FStar.wasm")
    val moduleHaclHashMd5 = readModule("input/Hacl_Hash_MD5.wasm")
    val moduleWasmSupport = readModule("input/WasmSupport.wasm")
    val moduleWasmSupportGenerated = readModule("input/WasmSupport.generated.wasm")

    val moduleWasmSupportJoined = linkModules(
        mapOf(
            "WasmSupport" to moduleWasmSupport,
            "WasmSupport.Generated" to moduleWasmSupportGenerated
        )
    ) { pool ->
        pool.renameImportModule(
            ImportIdentifier(
                module = "WasmSupport",
                name = "WasmSupport_malloc",
                sourceModule = "WasmSupport"
            ),
            "WasmSupport.Generated"
        )

        pool.renameImportModule(
            ImportIdentifier(
                module = "WasmSupport",
                name = "WasmSupport_trap",
                sourceModule = "WasmSupport"
            ),
            "WasmSupport.Generated"
        )
    }

    val moduleLinked = linkModules(
        mapOf(
            "FStar" to moduleFStar,
            "Hacl_Hash_MD5" to moduleHaclHashMd5,
            "WasmSupport" to moduleWasmSupportJoined,
        )
    ) { pool ->
        pool.rename(
            ExportIdentifier(
                module = "FStar",
                names = setOf("data_size")
            ),
            "FStar_data_size"
        )

        pool.rename(
            ImportIdentifier(
                module = "FStar",
                name = "data_start",
                sourceModule = "Karamel"
            ),
            "FStar_data_start"
        )

        pool.rename(
            ExportIdentifier(
                module = "Hacl_Hash_MD5",
                names = setOf("data_size")
            ),
            "Hacl_Hash_MD5_data_size"
        )

        pool.rename(
            ImportIdentifier(
                module = "Hacl_Hash_MD5",
                name = "data_start",
                sourceModule = "Karamel"
            ),
            "Hacl_Hash_MD5_data_start"
        )

        pool.rename(
            ExportIdentifier(
                module = "WasmSupport",
                names = setOf("data_size")
            ),
            "WasmSupport_data_size"
        )

        pool.rename(
            ImportIdentifier(
                module = "WasmSupport",
                name = "data_start",
                sourceModule = "Karamel"
            ),
            "WasmSupport_data_start"
        )
    }

    writeModule("LINKED.wasm", moduleLinked)
}
