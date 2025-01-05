package dev.fir3.wedding.linker.linking

import dev.fir3.wedding.linker.pool.*
import dev.fir3.wedding.linker.pool.Function

fun Pool.link(): Set<Conflict> {
    val exports = mutableMapOf<Pair<String, String>, Object>()
    val imports = mutableMapOf<Pair<String, String>, Object>()
    val conflicts = mutableSetOf<Conflict>()

    // Collect imports and exports

    val objects = functions + tables + memories + globals

    for (`object` in objects) {
        val assignedName = `object`[AssignedName::class]
            ?.let(AssignedName::name)
            ?: `object`[SourceName::class]?.let(SourceName::name)

        if (assignedName != null) {
            val sourceModule = `object`[SourceModule::class]!!.name

            exports[Pair(sourceModule, assignedName)] = `object`
        }

        val importModule = `object`[AssignedImportModule::class]?.name
            ?: `object`[ImportModule::class]?.let(ImportModule::name)

        val importName = `object`[AssignedImportName::class]?.name
            ?: `object`[ImportName::class]?.let(ImportName::name)

        if (importModule != null && importName != null) {
            imports[Pair(importModule, importName)] = `object`
        }
    }

    // Link imports with exports, if possible.

    for ((identifier, import) in imports) {
        val export = exports[identifier] ?: continue

        if (!import.isImportCompatibleWith(export, this)) {
            conflicts += Conflict(
                export = export.identifier,
                import = import.identifier
            )

            continue
        }

        import[ImportResolution::class] = ImportResolution(
            export[SourceIndex::class]!!.index
        )
    }

    return conflicts
}

private fun Object.isImportCompatibleWith(
    `object`: Object,
    pool: Pool
): Boolean {
    when (this) {
        is Function -> {
            if (`object` !is Function) return false

            val ourModule = this[SourceModule::class]!!.name
            val ourTypeIndex = this[FunctionTypeIndex::class]!!.typeIndex
            val theirModule = `object`[SourceModule::class]!!.name
            val theirTypeIndex = `object`[FunctionTypeIndex::class]!!.typeIndex

            val ourTypeObject = pool.resolve(
                IndexIdentifier(
                    index = ourTypeIndex,
                    module = ourModule,
                    type = ObjectType.FUNCTION_TYPE
                )
            )

            val theirTypeObject = pool.resolve(
                IndexIdentifier(
                    index = theirTypeIndex,
                    module = theirModule,
                    type = ObjectType.FUNCTION_TYPE
                )
            )

            val ourType = ourTypeObject!![FunctionTypeInfo::class]!!.type
            val theirType = theirTypeObject!![FunctionTypeInfo::class]!!.type

            return ourType == theirType
        }

        is Global -> {
            if (`object` !is Global) return false

            val ourTypeInfo = this[GlobalType::class]!!
            val theirTypeInfo = `object`[GlobalType::class]!!

            if (ourTypeInfo.isMutable && !theirTypeInfo.isMutable) return false

            return ourTypeInfo.type == theirTypeInfo.type
        }

        is Memory -> {
            if (`object` !is Memory) return false

            val ourLimits = this[MemoryInfo::class]!!.limits
            val theirLimits = `object`[MemoryInfo::class]!!.limits

            return ourLimits == theirLimits
        }
        is Table -> {
            if (`object` !is Table) return false

            val ourType = this[TableInfo::class]!!.type
            val theirType = `object`[TableInfo::class]!!.type

            return ourType == theirType
        }
        else -> return false
    }
}
