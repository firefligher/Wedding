package dev.fir3.wedding.linker.pool

import kotlin.reflect.KClass

sealed interface Identifier {
    val module: String
}

data class ExportIdentifier(
    override val module: String,
    val names: Set<String>
) : Identifier {
    override fun equals(other: Any?): Boolean {
        if (other !is ExportIdentifier) return false
        if (other.module != module) return false

        return names.intersect(other.names).isNotEmpty()
    }

    override fun hashCode(): Int {
        return module.hashCode()
    }
}

data class ImportIdentifier(
    override val module: String,
    val name: String,
    val sourceModule: String
) : Identifier

data class IndexIdentifier(
    val index: UInt,
    override val module: String,
    val type: ObjectType
) : Identifier

enum class ObjectType {
    DATA,
    ELEMENT,
    FUNCTION,
    FUNCTION_TYPE,
    GLOBAL,
    MEMORY,
    TABLE
}

fun makeIdentifier(
    objectType: ObjectType,
    annotations: Map<KClass<out Annotation>, Annotation>
): Identifier {
    val sourceModule = (annotations[SourceModule::class] as SourceModule).name
    val sourceNames = annotations[AssignedName::class]
        ?.let { annotation -> setOf((annotation as AssignedName).name) }
        ?: annotations[SourceNames::class]
            ?.let { annotation -> annotation as SourceNames }?.names

    if (sourceNames != null) {
        return ExportIdentifier(
            module = sourceModule,
            names = sourceNames
        )
    }

    val importModule = annotations[ImportModule::class]?.let { annotation ->
        annotation as ImportModule
    }?.name

    val importName = annotations[AssignedImportName::class]
        ?.let { annotation -> annotation as AssignedImportName }?.name
        ?: annotations[ImportName::class]
            ?.let { annotation -> annotation as ImportName }?.name

    if (importModule != null && importName != null) {
        return ImportIdentifier(
            module = sourceModule,
            name = importName,
            sourceModule = importModule
        )
    }

    val sourceIndex = annotations[SourceIndex::class] as SourceIndex

    return IndexIdentifier(
        index = sourceIndex.index,
        module = sourceModule,
        type = objectType
    )
}
