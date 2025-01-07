package dev.fir3.wedding.linker.merging

import dev.fir3.wedding.linker.pool.*
import dev.fir3.wedding.wasm.FunctionType

fun Pool.mergeFunctionImports() = mergeImports(functions, this)
fun Pool.mergeGlobalImports() = mergeImports(globals, this)
fun Pool.mergeMemoryImports() = mergeImports(memories, this)
fun Pool.mergeTableImports() = mergeImports(tables, this)

fun Pool.mergeFunctionTypes() {
    val indices = mutableMapOf<FunctionType, Pair<String, UInt>>()

    for (functionType in functionTypes) {
        val type = functionType[FunctionTypeInfo::class]!!.type
        val index = indices[type]

        if (index != null) {
            val (module, typeIndex) = index

            functionType[FunctionTypeDuplicate::class] = FunctionTypeDuplicate(
                module = module,
                typeIndex = typeIndex
            )

            continue
        }

        val sourceModule = functionType[SourceModule::class]!!.name
        val sourceIndex = functionType[SourceIndex::class]!!.index

        indices[type] = Pair(sourceModule, sourceIndex)
    }
}

private fun mergeImports(objects: Collection<Object>, pool: Pool) {
    val imports = mutableMapOf<Pair<String, String>, Object>()

    for (`object` in objects) {
        val importModule = `object`[AssignedImportModule::class]?.name
            ?: `object`[ImportModule::class]?.name

        val importName = `object`[AssignedImportName::class]?.name
            ?: `object`[ImportName::class]?.name

        if (importModule == null || importName == null) continue

        val identifier = Pair(importModule, importName)
        val import = imports[identifier]

        if (import != null) {
            // Since isImportCompatibleWith is not necessarily commutative, we
            // need to cover both cases.

            if (`object`.isImportCompatibleWith(import, pool)) {
                val sourceModule = import[SourceModule::class]!!.name
                val sourceIndex = import[SourceIndex::class]!!.index

                `object`[ImportDuplicate::class] = ImportDuplicate(
                    sourceModule,
                    sourceIndex
                )

                continue
            } else if (import.isImportCompatibleWith(`object`, pool)) {
                val sourceModule = `object`[SourceModule::class]!!.name
                val sourceIndex = `object`[SourceIndex::class]!!.index

                import[ImportDuplicate::class] = ImportDuplicate(
                    sourceModule,
                    sourceIndex
                )
            }
        }

        imports[identifier] = `object`
    }
}
