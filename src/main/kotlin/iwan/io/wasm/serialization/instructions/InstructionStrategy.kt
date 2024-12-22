package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.common.ClassLoaderUtilities
import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.serialization.SerializationStrategy
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.write
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readUInt8
import dev.fir3.iwan.io.wasm.models.instructions.*
import java.io.IOException
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

private fun createDescriptor(
    instructionClass: KClass<out Instruction>,
    instructionInstance: Instruction?,
    opcode: UByte,
    strategy: InstructionSerializationStrategy<*>
): SerializationDescriptor<*> {
    val ctor = SerializationDescriptor::class.primaryConstructor!!
    ctor.isAccessible = true
    return ctor.call(
        instructionClass,
        instructionInstance,
        opcode,
        strategy
    )
}

internal object InstructionStrategy :
    DeserializationStrategy<Instruction>,
    SerializationStrategy<Instruction> {
    private val deserializationDescriptors: Map<
            UByte,
            SerializationDescriptor<*>
    >

    private val serializationStrategies: Map<
            KClass<out Instruction>,
            SerializationDescriptor<*>
    >

    init {
        val classes = ClassLoaderUtilities.queryClasses(
            Instruction::class.java.classLoader,
            Instruction::class.java.`package`.name
        ).mapNotNull { clazz ->
            if (!Instruction::class.java.isAssignableFrom(clazz)) null
            else {
                // Safe cast due to the assignability.

                @Suppress("UNCHECKED_CAST")
                clazz as Class<out Instruction>
            }
        }

        val descriptorEntries = classes.mapNotNull { clazz ->
            val info = clazz.getAnnotation(InstructionInfo::class.java)
                ?: return@mapNotNull null

            val strategy = info
                .strategy
                .objectInstance
                ?: throw IllegalStateException(
                    "Serialization strategy is no object"
                )

            createDescriptor(
                clazz.kotlin,
                null,
                info.instructionId,
                strategy
            )
        }.toMutableList()

        descriptorEntries += classes.flatMap { clazz ->
            if (!clazz.isEnum) return@flatMap emptyList()

            clazz.enumConstants.mapNotNull { constant ->
                val constantName = (constant as Enum<*>).name

                val info = clazz.getDeclaredField(constantName).getAnnotation(
                    InstructionInfo::class.java
                ) ?: return@mapNotNull null

                val strategy = info
                    .strategy
                    .objectInstance
                    ?: throw IllegalStateException(
                        "Serialization strategy is no object"
                    )

                createDescriptor(
                    clazz.kotlin,
                    constant,
                    info.instructionId,
                    strategy
                )
            }
        }

        deserializationDescriptors = descriptorEntries
            .associateBy(SerializationDescriptor<*>::opcode)

        serializationStrategies = descriptorEntries
            .associateBy(SerializationDescriptor<*>::instructionClass)
    }

    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): Instruction {
        val instructionId = source.readUInt8()

        return deserializationDescriptors[instructionId]
            ?.deserialize(source, context)
            ?: throw IOException("Unsupported opcode: $instructionId")
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: Instruction
    ) {
        val clazz = value.javaClass.kotlin
        val descriptor = serializationStrategies[clazz]
            ?: serializationStrategies
                .filterKeys(clazz::isSubclassOf)
                .firstNotNullOf(
                    Map.Entry<
                            KClass<out Instruction>,
                            SerializationDescriptor<*>
                    >::value
                )

        sink.write(descriptor.opcode)
        descriptor.serialize(sink, context, value)
    }
}

private data class SerializationDescriptor<TInstruction : Instruction>(
    val instructionClass: KClass<TInstruction>,
    val instructionInstance: TInstruction?,
    val opcode: UByte,
    val strategy: InstructionSerializationStrategy<TInstruction>
) {
    fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ) = strategy.deserialize(
        source,
        context,
        instructionClass,
        instructionInstance
    )

    @Suppress("UNCHECKED_CAST")
    fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        instance: Instruction
    ) = strategy.serialize(sink, context, instance as TInstruction)
}
