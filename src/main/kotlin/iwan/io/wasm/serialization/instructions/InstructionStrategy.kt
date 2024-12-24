package dev.fir3.iwan.io.wasm.serialization.instructions

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
            Any,
            SerializationDescriptor<*>
    >

    init {
        // FIXME: Convert this to a SPI or similar. Apparently, class discovery
        //        via ClassLoader is undefined behavior.

        val classes = listOf(
            BlockInstruction::class.java,
            ConditionalBlockInstruction::class.java,
            LoopInstruction::class.java,
            UnconditionalBranchInstruction::class.java,
            ConditionalBranchInstruction::class.java,
            TableBranchInstruction::class.java,
            CallInstruction::class.java,
            CallInstruction::class.java,
            Float32ConstInstruction::class.java,
            Float64ConstInstruction::class.java,
            Int32ConstInstruction::class.java,
            Int64ConstInstruction::class.java,
            FlatInstruction::class.java,
            Float32LoadInstruction::class.java,
            Float32StoreInstruction::class.java,
            Float64LoadInstruction::class.java,
            Float64StoreInstruction::class.java,
            Int32LoadInstruction::class.java,
            Int32Load8SInstruction::class.java,
            Int32Load8UInstruction::class.java,
            Int32Load16SInstruction::class.java,
            Int32Load16UInstruction::class.java,
            Int32StoreInstruction::class.java,
            Int32Store8Instruction::class.java,
            Int32Store16Instruction::class.java,
            Int64LoadInstruction::class.java,
            Int64Load8SInstruction::class.java,
            Int64Load8UInstruction::class.java,
            Int64Load16SInstruction::class.java,
            Int64Load16UInstruction::class.java,
            Int64Load32SInstruction::class.java,
            Int64Load32UInstruction::class.java,
            Int64StoreInstruction::class.java,
            Int64Store8Instruction::class.java,
            Int64Store16Instruction::class.java,
            Int64Store32Instruction::class.java,
            MemoryGrowInstruction::class.java,
            MemorySizeInstruction::class.java,
            ReferenceFunctionInstruction::class.java,
            LocalGetInstruction::class.java,
            LocalSetInstruction::class.java,
            LocalTeeInstruction::class.java,
            GlobalGetInstruction::class.java,
            GlobalSetInstruction::class.java
        )

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
            .associateBy { descriptor ->
                descriptor.instructionInstance ?: descriptor.instructionClass
            }
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
        val descriptor = serializationStrategies[value]
            ?: serializationStrategies[clazz]
            ?: serializationStrategies
                .filterKeys { key ->
                    key is KClass<*> && clazz.isSubclassOf(key)
                }
                .firstNotNullOf(
                    Map.Entry<Any, SerializationDescriptor<*>>::value
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
