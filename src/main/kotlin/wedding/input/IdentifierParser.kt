package dev.fir3.wedding.input

import dev.fir3.iwan.io.wasm.models.FunctionType
import dev.fir3.iwan.io.wasm.models.GlobalType
import dev.fir3.iwan.io.wasm.models.MemoryType
import dev.fir3.iwan.io.wasm.models.valueTypes.NumberType
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType
import dev.fir3.iwan.io.wasm.models.valueTypes.ValueType
import dev.fir3.iwan.io.wasm.models.valueTypes.VectorType
import dev.fir3.wedding.input.model.identifier.*
import dev.fir3.wedding.input.model.identifier.ExportedFunctionIdentifier
import dev.fir3.wedding.input.model.identifier.ExportedGlobalIdentifier
import dev.fir3.wedding.input.model.identifier.Identifier
import dev.fir3.wedding.input.model.identifier.ImportedFunctionIdentifier

internal object IdentifierParser {
    fun parse(value: String): Identifier {
        val components = value.split('.')
        check(components.size == 4)

        val identifierType = IdentifierType.parse(components[0])
        val (module, sourceModule) = if (components[1].contains('@')) {
            val moduleComponents = components[1].split('@')
            check(moduleComponents.size == 2)

            Pair(
                moduleComponents[0],
                moduleComponents[1]
            )
        } else {
            Pair(components[1], null)
        }

        val objectName = components[2]
        val objectType = components[3]

        return when (identifierType) {
            IdentifierType.Function -> if (sourceModule == null) {
                ExportedFunctionIdentifier(
                    function = objectName,
                    module = module,
                    type = parseFunctionType(objectType)
                )
            } else {
                ImportedFunctionIdentifier(
                    function = objectName,
                    module = module,
                    sourceModule = sourceModule,
                    type = parseFunctionType(objectType)
                )
            }

            IdentifierType.Global -> if (sourceModule == null) {
                ExportedGlobalIdentifier(
                    global = objectName,
                    module = module,
                    type = parseGlobalType(objectType)
                )
            } else {
                ImportedGlobalIdentifier(
                    global = objectName,
                    module = module,
                    sourceModule = sourceModule,
                    type = parseGlobalType(objectType)
                )
            }

            IdentifierType.Memory -> if (sourceModule == null) {
                ExportedMemoryIdentifier(
                    memory = objectName,
                    module = module,
                    type = parseMemoryType(objectType)
                )
            } else {
                ImportedMemoryIdentifier(
                    memory = objectName,
                    module = module,
                    sourceModule = sourceModule,
                    type = parseMemoryType(objectType)
                )
            }
        }
    }

    fun stringify(identifier: Identifier): String {
        val identifierType: String
        val module: String
        val sourceModule: String?
        val objectName: String
        val objectType: String

        when (identifier) {
            is ExportedFunctionIdentifier -> {
                identifierType = IdentifierType.Function.identifier
                module = identifier.module
                sourceModule = null
                objectName = identifier.function
                objectType = identifier.type.stringify()
            }
            is ExportedGlobalIdentifier -> {
                identifierType = IdentifierType.Global.identifier
                module = identifier.module
                sourceModule = null
                objectName = identifier.global
                objectType = identifier.type.stringify()
            }
            is ExportedMemoryIdentifier -> {
                identifierType = IdentifierType.Memory.identifier
                module = identifier.module
                sourceModule = null
                objectName = identifier.memory
                objectType = identifier.type.stringify()
            }
            is ImportedFunctionIdentifier -> {
                identifierType = IdentifierType.Function.identifier
                module = identifier.module
                sourceModule = identifier.sourceModule
                objectName = identifier.function
                objectType = identifier.type.stringify()
            }
            is ImportedGlobalIdentifier -> {
                identifierType = IdentifierType.Global.identifier
                module = identifier.module
                sourceModule = identifier.sourceModule
                objectName = identifier.global
                objectType = identifier.type.stringify()
            }
            is ImportedMemoryIdentifier -> {
                identifierType = IdentifierType.Memory.identifier
                module = identifier.module
                sourceModule = identifier.sourceModule
                objectName = identifier.memory
                objectType = identifier.type.stringify()
            }
        }

        return if (sourceModule == null) {
            "$identifierType.$module.$objectName.$objectType"
        } else {
            "$identifierType.$module@$sourceModule.$objectName.$objectType"
        }
    }
}

private const val IDENTIFIER_FUNCTION = "F"
private const val IDENTIFIER_GLOBAL = "G"
private const val IDENTIFIER_MEMORY = "M"

private enum class IdentifierType(val identifier: String) {
    Function(IDENTIFIER_FUNCTION),
    Global(IDENTIFIER_GLOBAL),
    Memory(IDENTIFIER_MEMORY);

    companion object {
        fun parse(type: String) = when (type) {
            IDENTIFIER_FUNCTION -> Function
            IDENTIFIER_GLOBAL -> Global
            IDENTIFIER_MEMORY -> Memory
            else -> TODO()
        }
    }
}

private const val IDENTIFIER_MUTABLE = "m"
private const val IDENTIFIER_IMMUTABLE = "i"

private fun parseFunctionType(type: String): FunctionType {
    val components = type.split("->")
    check(components.size == 2)

    val parameterComponents = components[0]
        .substringAfter('(')
        .substringBefore(')')
        .split(',')

    val resultComponents = components[1]
        .substringAfter('(')
        .substringBefore(')')
        .split(',')

    return FunctionType(
        parameterTypes = parameterComponents.map(::parseValueType),
        resultTypes = resultComponents.map(::parseValueType)
    )
}

private fun parseGlobalType(type: String): GlobalType {
    val components = type
        .substringAfter('(')
        .substringBefore(')')
        .split(",")

    check(components.size == 2)

    val isMutable = when (components[0]) {
        IDENTIFIER_MUTABLE -> true
        IDENTIFIER_IMMUTABLE -> false
        else -> TODO()
    }

    val valueType = parseValueType(components[1])

    return GlobalType(
        isMutable = isMutable,
        valueType = valueType
    )
}

private fun parseMemoryType(type: String): MemoryType {
    val components = type
        .substringAfter('(')
        .substringBefore(')')
        .split(",")

    return when (components.size) {
        1 -> MemoryType(
            minimum = components[0].toUInt(),
            maximum = null
        )
        2 -> MemoryType(
            minimum = components[0].toUInt(),
            maximum = components[1].toUInt()
        )
        else -> TODO()
    }
}

private const val IDENTIFIER_FLOAT32 = "F32"
private const val IDENTIFIER_FLOAT64 = "F64"
private const val IDENTIFIER_INT32 = "I32"
private const val IDENTIFIER_INT64 = "I64"
private const val IDENTIFIER_EXTERNAL_REFERENCE = "E"
private const val IDENTIFIER_FUNCTION_REFERENCE = "F"
private const val IDENTIFIER_VECTOR128 = "V128"

private fun parseValueType(value: String): ValueType = when (value) {
    IDENTIFIER_FLOAT32 -> NumberType.Float32
    IDENTIFIER_FLOAT64 -> NumberType.Float64
    IDENTIFIER_INT32 -> NumberType.Int32
    IDENTIFIER_INT64 -> NumberType.Int64
    IDENTIFIER_EXTERNAL_REFERENCE -> ReferenceType.ExternalReference
    IDENTIFIER_FUNCTION_REFERENCE -> ReferenceType.FunctionReference
    IDENTIFIER_VECTOR128 -> VectorType.Vector128
    else -> TODO()
}

private fun FunctionType.stringify(): String {
    val b = StringBuilder("(")
    var first = true

    for (parameterType in parameterTypes) {
        if (!first) b.append(',')
        b.append(parameterType.stringify())
        first = false
    }

    b.append(")->(")
    first = true

    for (resultType in resultTypes) {
        if (!first) b.append(',')
        b.append(resultType.stringify())
        first = false
    }

    return b.append(')').toString()
}

private fun GlobalType.stringify(): String {
    val b = StringBuilder("(")

    if (isMutable) b.append(IDENTIFIER_MUTABLE)
    else b.append(IDENTIFIER_IMMUTABLE)

    return b
        .append(",")
        .append(valueType.stringify())
        .append(')')
        .toString()
}

private fun MemoryType.stringify(): String {
    val b = StringBuilder("(").append(minimum)
    maximum?.let { maximum -> b.append(",").append(maximum) }
    return b.append(')').toString()
}

private fun ValueType.stringify(): String = when (this) {
    NumberType.Float32 -> IDENTIFIER_FLOAT32
    NumberType.Float64 -> IDENTIFIER_FLOAT64
    NumberType.Int32 -> IDENTIFIER_INT32
    NumberType.Int64 -> IDENTIFIER_INT64
    ReferenceType.ExternalReference -> IDENTIFIER_EXTERNAL_REFERENCE
    ReferenceType.FunctionReference -> IDENTIFIER_FUNCTION_REFERENCE
    VectorType.Vector128 -> IDENTIFIER_VECTOR128
}
