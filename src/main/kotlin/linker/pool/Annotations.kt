package dev.fir3.wedding.linker.pool

import dev.fir3.wedding.wasm.*
import dev.fir3.wedding.wasm.FunctionType

sealed interface Annotation

// Annotations for all objects

data class RelocatedIndex(val index: UInt) : Annotation
data class SourceModule(val name: String) : Annotation
data class SourceIndex(val index: UInt) : Annotation

// Annotations for exportable objects

data class AssignedName(val name: String) : Annotation
data class SourceName(val name: String) : Annotation

// Annotations for imports

data class AssignedImportModule(val name: String) : Annotation
data class AssignedImportName(val name: String) : Annotation
data class ImportModule(val name: String) : Annotation
data class ImportName(val name: String) : Annotation
data class ImportResolution(val index: UInt) : Annotation

// Annotations for object-type-specific information

data class ActiveDataInfo(
    val memoryIndex: UInt,
    val offset: List<Instruction>
) : Annotation

data class FixedActiveDataInfo(
    val memoryIndex: UInt,
    val offset: List<Instruction>
) : Annotation

data class DataContent(val content: ByteArray) : Annotation {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataContent

        return content.contentEquals(other.content)
    }

    override fun hashCode(): Int {
        return content.contentHashCode()
    }
}

data class ActiveElementInfo(
    val offset: List<Instruction>,
    val tableIndex: UInt
) : Annotation

data class FixedActiveElementInfo(
    val offset: List<Instruction>,
    val tableIndex: UInt
) : Annotation

data object DeclarativeElement : Annotation
data class ElementInfo(
    val initializers: List<List<Instruction>>,
    val type: ReferenceType
) : Annotation

data class FixedElementInitializers(
    val initializers: List<List<Instruction>>
) : Annotation

data class FixedFunctionBody(val code: Code) : Annotation
data class FixedFunctionTypeIndex(val typeIndex: UInt) : Annotation
data class FunctionBody(val code: Code) : Annotation
data class FunctionTypeIndex(val typeIndex: UInt) : Annotation
data class FunctionTypeInfo(val type: FunctionType) : Annotation
data class FunctionTypeDuplicate(
    val module: String,
    val typeIndex: UInt
) : Annotation

data class FixedGlobalInitializer(
    val instructions: List<Instruction>
) : Annotation

data class GlobalInitializer(val instructions: List<Instruction>) : Annotation
data class GlobalType(val isMutable: Boolean, val type: ValueType) : Annotation
data class MemoryInfo(val limits: Limits) : Annotation
data object StartFunction : Annotation
data class TableInfo(val type: TableType) : Annotation
