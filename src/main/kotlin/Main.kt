package dev.fir3.wedding

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
import dev.fir3.wedding.linker.pool.Data
import dev.fir3.wedding.linker.pool.FunctionType
import dev.fir3.wedding.linker.relocation.*
import dev.fir3.wedding.linker.renaming.rename
import dev.fir3.wedding.linker.renaming.renameImport
import dev.fir3.wedding.linker.renaming.renameImportModule
import dev.fir3.wedding.linker.sanity.checkForDuplicateExports
import dev.fir3.wedding.wasm.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID
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

    pool.linkStartFunctions(UUID.randomUUID().toString())
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
    val modulesPaths = mapOf(
        "FStar" to "input/FStar.wasm",
        "Hacl_Bignum25519_51" to "input/Hacl_Bignum25519_51.wasm",
        "Hacl_Curve25519_51" to "input/Hacl_Curve25519_51.wasm",
        "Hacl_Ed25519" to "input/Hacl_Ed25519.wasm",
        "Hacl_Ed25519_PrecompTable" to "input/Hacl_Ed25519_PrecompTable.wasm",
        "Hacl_Hash_SHA2" to "input/Hacl_Hash_SHA2.wasm",
        "Polyfill" to "polyfill/Polyfill.wasm",
        "WasmSupport" to "input/WasmSupport.wasm"
    )

    val modules = modulesPaths.mapValues { (_, path) ->
        readModule(path)
    }

    val linkedModule = linkModules(modules) { pool ->
        // Support module for the polyfill

        val supportModule = UUID.randomUUID().toString()

        pool.add(
            FunctionType(
                mutableMapOf(
                    SourceModule::class to SourceModule(supportModule),
                    SourceIndex::class to SourceIndex(0u),
                    FunctionTypeInfo::class to FunctionTypeInfo(
                        dev.fir3.wedding.wasm.FunctionType(
                            listOf(NumberType.INT32),
                            emptyList()
                        )
                    )
                )
            )
        )

        pool.add(
            Function(
                mutableMapOf(
                    SourceModule::class to SourceModule(supportModule),
                    SourceIndex::class to SourceIndex(0u),
                    FunctionTypeIndex::class to FunctionTypeIndex(0u),
                    FunctionBody::class to FunctionBody(
                        Code(
                            listOf(
                                Int32ConstInstruction(0),
                                LocalGetInstruction(0u),
                                Int32StoreInstruction(0u, 0u)
                            ),
                            emptyList()
                        )
                    ),
                    AssignedName::class to
                            AssignedName("Support_SetHaclStackPtr")
                )
            )
        )

        pool.add(
            FunctionType(
                mutableMapOf(
                    SourceModule::class to SourceModule(supportModule),
                    SourceIndex::class to SourceIndex(1u),
                    FunctionTypeInfo::class to FunctionTypeInfo(
                        dev.fir3.wedding.wasm.FunctionType(
                            emptyList(),
                            listOf(NumberType.INT32)
                        )
                    )
                )
            )
        )

        pool.add(
            Function(
                mutableMapOf(
                    SourceModule::class to SourceModule(supportModule),
                    SourceIndex::class to SourceIndex(1u),
                    FunctionTypeIndex::class to FunctionTypeIndex(1u),
                    FunctionBody::class to FunctionBody(
                        Code(
                            listOf(
                                Int32ConstInstruction(0),
                                Int32LoadInstruction(0u, 0u)
                            ),
                            emptyList()
                        )
                    ),
                    AssignedName::class to
                            AssignedName("Support_GetHaclStackPtr")
                )
            )
        )

        // Polyfill modifications.

        pool.renameImportModule(
            identifier = ImportIdentifier(
                module = "Polyfill",
                name = "Hacl_Hash_SHA2_hash_256",
                sourceModule = "env"
            ),
            newModule = "Hacl_Hash_SHA2"
        )

        pool.renameImportModule(
            identifier = ImportIdentifier(
                module = "Polyfill",
                name = "Hacl_Hash_SHA2_hash_512",
                sourceModule = "env"
            ),
            newModule = "Hacl_Hash_SHA2"
        )

        pool.renameImportModule(
            identifier = ImportIdentifier(
                module = "Polyfill",
                name = "Hacl_Ed25519_sign",
                sourceModule = "env"
            ),
            newModule = "Hacl_Ed25519"
        )

        pool.renameImportModule(
            identifier = ImportIdentifier(
                module = "Polyfill",
                name = "Support_GetHaclStackPtr",
                sourceModule = "env"
            ),
            newModule = supportModule
        )

        pool.renameImportModule(
            identifier = ImportIdentifier(
                module = "Polyfill",
                name = "Support_SetHaclStackPtr",
                sourceModule = "env"
            ),
            newModule = supportModule
        )

        // Some other code.

        var dataOffset = 65536u + 128u

        modules.keys.forEach { moduleName ->
            if (moduleName == "Polyfill") return@forEach

            // Rename things.

            pool.rename(
                identifier = ExportIdentifier(
                    module = moduleName,
                    names = setOf("data_size")
                ),
                newName = "R${moduleName}_data_size"
            )

            pool.renameImportModule(
                identifier = ImportIdentifier(
                    module = moduleName,
                    name = "WasmSupport_malloc",
                    sourceModule = "WasmSupport"
                ),
                newModule = "Polyfill"
            )

            pool.renameImportModule(
                identifier = ImportIdentifier(
                    module = moduleName,
                    name = "WasmSupport_trap",
                    sourceModule = "WasmSupport"
                ),
                newModule = "Polyfill"
            )

            pool.renameImport(
                identifier = ImportIdentifier(
                    module = moduleName,
                    name = "mem",
                    sourceModule = "Karamel"
                ),
                newName = "memory"
            )

            pool.renameImportModule(
                identifier = ImportIdentifier(
                    module = moduleName,
                    name = "memory",
                    sourceModule = "Karamel"
                ),
                newModule = "Polyfill"
            )

            // Fix Memory Layout.

            val dataSizeGlobal = pool.resolve(
                ExportIdentifier(
                    module = moduleName,
                    names = setOf("R${moduleName}_data_size"),
                )
            )

            val dataSizeInstruction =
                dataSizeGlobal!![GlobalInitializer::class]!!
                    .instructions
                    .single() as Int32ConstInstruction

            val dataStartGlobal = pool.resolve(
                identifier = ImportIdentifier(
                    module = moduleName,
                    name = "data_start",
                    sourceModule = "Karamel"
                ),
            )

            setOf(
                AssignedImportModule::class,
                AssignedImportName::class,
                ImportDuplicate::class,
                ImportModule::class,
                ImportName::class,
                ImportResolution::class
            ).forEach {
                dataStartGlobal!!.annotations.remove(it)
            }

            dataStartGlobal!![GlobalInitializer::class] = GlobalInitializer(
                instructions = listOf(
                    Int32ConstInstruction(dataOffset.toInt())
                )
            )

            dataStartGlobal[AssignedName::class] =
                AssignedName("${moduleName}_data_start")

            dataOffset += dataSizeInstruction.constant.toUInt()
        }

        // Add some data that initializes the HACL*'s memory header.

        val headerModule = UUID.randomUUID().toString()

        pool.add(
            Memory(
                mutableMapOf(
                    SourceModule::class to SourceModule(headerModule),
                    ImportModule::class to ImportModule("Polyfill"),
                    ImportName::class to ImportName("memory"),
                    SourceIndex::class to SourceIndex(0u),
                    MemoryInfo::class to MemoryInfo(
                        Limits(1u, null)
                    )
                )
            )
        )

        pool.add(
            Data(
                mutableMapOf(
                    SourceModule::class to SourceModule(headerModule),
                    SourceIndex::class to SourceIndex(0u),
                    DataContent::class to DataContent(
                        byteArrayOf(
                            (dataOffset and 0xFFu).toByte(),
                            ((dataOffset shr 8) and 0xFFu).toByte(),
                            ((dataOffset shr 16) and 0xFFu).toByte(),
                            ((dataOffset shr 24) and 0xFFu).toByte()
                        )
                    ),
                    ActiveDataInfo::class to ActiveDataInfo(
                        memoryIndex = 0u,
                        offset = listOf(
                            Int32ConstInstruction(0)
                        )
                    )
                )
            )
        )
    }

    writeModule("LINKED.wasm", linkedModule)
}
