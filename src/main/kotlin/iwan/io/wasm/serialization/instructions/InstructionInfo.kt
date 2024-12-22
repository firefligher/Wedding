package dev.fir3.iwan.io.wasm.serialization.instructions

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
internal annotation class InstructionInfo<
        TStrategy : InstructionSerializationStrategy<*>
>(
    val instructionId: UByte,
    val strategy: KClass<TStrategy>
)
