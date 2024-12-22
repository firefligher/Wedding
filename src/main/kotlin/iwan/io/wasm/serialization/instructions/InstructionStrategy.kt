package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.common.ClassLoaderUtilities
import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.readUInt8
import dev.fir3.iwan.io.wasm.models.instructions.*
import java.io.IOException
import kotlin.reflect.KClass

internal object InstructionStrategy : DeserializationStrategy<Instruction> {
    private val descriptors: Map<UByte, DeserializationDescriptor>

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

            val strategy = info.strategy.objectInstance
                ?: throw IllegalStateException(
                    "Deserialization strategy is no object"
                )

            Pair(
                info.instructionId,
                DeserializationDescriptor(strategy, clazz.kotlin, null)
            )
        }.toMutableList()

        descriptorEntries += classes.flatMap { clazz ->
            if (!clazz.isEnum) return@flatMap emptyList()

            clazz.enumConstants.mapNotNull { constant ->
                val constantName = (constant as Enum<*>).name

                val info = clazz.getDeclaredField(constantName).getAnnotation(
                    InstructionInfo::class.java
                ) ?: return@mapNotNull null

                val strategy = info.strategy.objectInstance
                    ?: throw IllegalStateException(
                        "Deserialization strategy is no object"
                    )

                Pair(
                    info.instructionId,
                    DeserializationDescriptor(strategy, clazz.kotlin, constant)
                )
            }
        }

        descriptors = descriptorEntries.toMap()
    }

    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ): Instruction {
        val instructionId = source.readUInt8()

        return descriptors[instructionId]?.let { descriptor ->
            descriptor.strategy.deserialize(
                source,
                context,
                descriptor.instructionClass,
                descriptor.instructionInstance
            )
        } ?: throw IOException("Unsupported opcode: $instructionId")
    }
}

private data class DeserializationDescriptor(
    val strategy: InstructionDeserializationStrategy,
    val instructionClass: KClass<out Instruction>,
    val instructionInstance: Instruction?
)
