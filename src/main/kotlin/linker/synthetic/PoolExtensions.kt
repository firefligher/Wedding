package dev.fir3.wedding.linker.code

import dev.fir3.wedding.cli.StorableType
import dev.fir3.wedding.cli.StorableType.*
import dev.fir3.wedding.linker.pool.*
import dev.fir3.wedding.linker.pool.Function
import dev.fir3.wedding.linker.pool.FunctionType
import dev.fir3.wedding.linker.pool.Global
import dev.fir3.wedding.wasm.*

private const val MODULE = "__SYNTHETIC"
private var nextTypeIndex = 0u
private var nextFunctionIndex = 0u
private var nextGlobal = 0u

fun Pool.addGetter(
    name: String,
    address: UInt,
    type: StorableType
): Function {
    add(
        FunctionType(
            mutableMapOf(
                SourceModule::class to SourceModule(MODULE),
                SourceIndex::class to SourceIndex(nextTypeIndex++),
                FunctionTypeInfo::class to FunctionTypeInfo(
                    dev.fir3.wedding.wasm.FunctionType(
                        emptyList(),
                        listOf(type.wasmType)
                    )
                )
            )
        )
    )

    val function = Function(
        mutableMapOf(
            SourceModule::class to SourceModule(MODULE),
            SourceIndex::class to SourceIndex(nextFunctionIndex++),
            FunctionTypeIndex::class to FunctionTypeIndex(nextTypeIndex - 1u),
            FunctionBody::class to FunctionBody(
                Code(
                    listOf(
                        Int32ConstInstruction(address.toInt()),
                        when (type) {
                            FLOAT32 -> Float32LoadInstruction(0u, 0u)
                            FLOAT64 -> Float64LoadInstruction(0u, 0u)
                            INT8S -> Int32Load8SInstruction(0u, 0u)
                            INT8U -> Int32Load8UInstruction(0u, 0u)
                            INT16S -> Int32Load16SInstruction(0u, 0u)
                            INT16U -> Int32Load16UInstruction(0u, 0u)
                            INT32 -> Int32LoadInstruction(0u, 0u)
                            INT64 -> Int64LoadInstruction(0u, 0u)
                        }
                    ),
                    emptyList()
                )
            ),
            AssignedName::class to AssignedName(name)
        )
    )

    add(function)
    return function
}

fun Pool.addSetter(
    name: String,
    address: UInt,
    type: StorableType
): Function {
    add(
        FunctionType(
            mutableMapOf(
                SourceModule::class to SourceModule(MODULE),
                SourceIndex::class to SourceIndex(nextTypeIndex++),
                FunctionTypeInfo::class to FunctionTypeInfo(
                    dev.fir3.wedding.wasm.FunctionType(
                        listOf(type.wasmType),
                        emptyList()
                    )
                )
            )
        )
    )

    val function = Function(
        mutableMapOf(
            SourceModule::class to SourceModule(MODULE),
            SourceIndex::class to SourceIndex(nextFunctionIndex++),
            FunctionTypeIndex::class to FunctionTypeIndex(nextTypeIndex - 1u),
            FunctionBody::class to FunctionBody(
                Code(
                    listOf(
                        Int32ConstInstruction(address.toInt()),
                        LocalGetInstruction(0u),
                        when (type) {
                            FLOAT32 -> Float32StoreInstruction(0u, 0u)
                            FLOAT64 -> Float64StoreInstruction(0u, 0u)
                            INT8S,
                            INT8U -> Int32Store8Instruction(0u, 0u)
                            INT16S,
                            INT16U -> Int32Store16Instruction(0u, 0u)
                            INT32 -> Int32StoreInstruction(0u, 0u)
                            INT64 -> Int64StoreInstruction(0u, 0u)
                        }
                    ),
                    emptyList()
                )
            ),
            AssignedName::class to AssignedName(name)
        )
    )

    add(function)
    return function
}

fun Pool.addGlobal(
    name: String,
    isMutable: Boolean,
    type: StorableType,
    initialValue: Number
): Global {
    val global = Global(
        mutableMapOf(
            SourceModule::class to SourceModule(MODULE),
            SourceIndex::class to SourceIndex(nextGlobal++),
            AssignedName::class to AssignedName(name),
            GlobalInitializer::class to GlobalInitializer(
                listOf(
                    when (type) {
                        FLOAT32 ->
                            Float32ConstInstruction(initialValue.toFloat())

                        FLOAT64 ->
                            Float64ConstInstruction(initialValue.toDouble())

                        INT8S,
                        INT8U,
                        INT16S,
                        INT16U,
                        INT32 -> Int32ConstInstruction(initialValue.toInt())
                        INT64 -> Int64ConstInstruction(initialValue.toLong())
                    }
                )
            ),
            GlobalType::class to GlobalType(
                isMutable,
                when (type) {
                    FLOAT32 -> NumberType.FLOAT32
                    FLOAT64 -> NumberType.FLOAT64
                    INT8S,
                    INT8U,
                    INT16S,
                    INT16U,
                    INT32 -> NumberType.INT32
                    INT64 -> NumberType.INT64
                }
            )
        )
    )

    add(global)
    return global
}
