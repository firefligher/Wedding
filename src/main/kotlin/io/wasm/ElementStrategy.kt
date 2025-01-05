package dev.fir3.wedding.io.wasm

import dev.fir3.wedding.io.foundation.ByteSink
import dev.fir3.wedding.io.foundation.ByteSource
import dev.fir3.wedding.io.serialization.Context
import dev.fir3.wedding.io.serialization.SerializationException
import dev.fir3.wedding.io.serialization.Strategy
import dev.fir3.wedding.io.serialization.deserialize
import dev.fir3.wedding.wasm.*

object ElementStrategy : Strategy<Element> {
    override fun deserialize(
        source: ByteSource,
        context: Context
    ) = when (val type = source.readVarUInt32()) {
        0u -> {
            val expression = context.deserializeVector<Instruction>(source)
            val functionIndices = source.readVarUInt32Vector()

            ActiveElement(
                functionIndices.map { index ->
                    listOf(ReferenceFunctionInstruction(index))
                },
                expression,
                0u,
                ReferenceType.FUNCTION
            )
        }

        1u -> {
            source.expect(0)
            val functionIndices = source.readVarUInt32Vector()

            PassiveElement(
                functionIndices.map { index ->
                    listOf(ReferenceFunctionInstruction(index))
                },
                ReferenceType.FUNCTION,
            )
        }

        2u -> {
            val tableIndex = source.readVarUInt32()
            val expression = context.deserializeVector<Instruction>(source)
            source.expect(0)
            val functionIndices = source.readVarUInt32Vector()

            ActiveElement(
                functionIndices.map { index ->
                    listOf(ReferenceFunctionInstruction(index))
                },
                expression,
                tableIndex,
                ReferenceType.FUNCTION
            )
        }

        3u -> {
            source.expect(0)
            val functionIndices = source.readVarUInt32Vector()

            DeclarativeElement(
                functionIndices.map { index ->
                    listOf(ReferenceFunctionInstruction(index))
                },
                ReferenceType.FUNCTION
            )
        }

        4u -> {
            val expression = context.deserializeVector<Instruction>(source)
            val expressionsCount = source.readVarUInt32()
            val expressions = mutableListOf<List<Instruction>>()

            for (i in 0u until expressionsCount) {
                expressions.add(context.deserializeVector<Instruction>(source))
            }

            ActiveElement(
                expressions,
                expression,
                0u,
                ReferenceType.FUNCTION
            )
        }

        5u -> {
            val referenceType = context.deserialize<ReferenceType>(source)
            val expressionsCount = source.readVarUInt32()
            val expressions = mutableListOf<List<Instruction>>()

            for (i in 0u until expressionsCount) {
                expressions.add(context.deserialize(source))
            }

            PassiveElement(expressions, referenceType)
        }

        6u -> {
            val tableIndex = source.readVarUInt32()
            val expression = context.deserializeVector<Instruction>(source)
            val referenceType = context.deserialize<ReferenceType>(source)
            val expressionsCount = source.readVarUInt32()
            val expressions = mutableListOf<List<Instruction>>()

            for (i in 0u until expressionsCount) {
                expressions.add(context.deserializeVector(source))
            }

            ActiveElement(
                expressions,
                expression,
                tableIndex,
                referenceType
            )
        }

        7u -> {
            val referenceType = context.deserialize<ReferenceType>(source)
            val expressionsCount = source.readVarUInt32()
            val expressions = mutableListOf<List<Instruction>>()

            for (i in 0u until expressionsCount) {
                expressions.add(context.deserializeVector<Instruction>(source))
            }

            DeclarativeElement(expressions, referenceType)
        }

        else -> throw SerializationException(
            "Unknown element segment type: $type"
        )
    }

    override fun serialize(
        instance: Element,
        sink: ByteSink,
        context: Context
    ) = when (instance) {
        is ActiveElement -> {
            val writeTable = instance.table > 0u
            val writeInitializersAsExpressions = instance
                .initializers
                .any { e ->
                    e.size > 1 || e.single() !is ReferenceFunctionInstruction
                }

            if (writeTable && writeInitializersAsExpressions)
                sink.writeVarUInt32(6u)
            else if (writeTable)
                sink.writeVarUInt32(2u)
            else if (writeInitializersAsExpressions)
                sink.writeVarUInt32(4u)
            else
                sink.writeVarUInt32(0u)

            if (writeTable) sink.writeVarUInt32(instance.table)
            context.serializeVector(sink, instance.offset)
            if (writeTable) context.serialize(sink, instance.type)

            sink.writeVarUInt32(instance.initializers.size.toUInt())
            instance.initializers.forEach { expression ->
                if (writeInitializersAsExpressions) {
                    context.serializeVector(sink, expression)
                } else {
                    val instruction = expression
                        .single() as ReferenceFunctionInstruction

                    sink.writeVarUInt32(instruction.functionIndex)
                }
            }
        }

        is DeclarativeElement -> {
            val writeInitializersAsExpressions = instance
                .initializers
                .any { e ->
                    e.size > 1 || e.single() !is ReferenceFunctionInstruction
                }

            if (writeInitializersAsExpressions)
                sink.writeVarUInt32(7u)
            else
                sink.writeVarUInt32(3u)

            context.serialize(sink, instance.type)

            sink.writeVarUInt32(instance.initializers.size.toUInt())
            instance.initializers.forEach { expression ->
                if (writeInitializersAsExpressions) {
                    context.serializeVector(sink, expression)
                } else {
                    val instruction = expression
                        .single() as ReferenceFunctionInstruction

                    sink.writeVarUInt32(instruction.functionIndex)
                }
            }
        }
        is PassiveElement -> {
            val writeInitializersAsExpressions = instance
                .initializers
                .any { e ->
                    e.size > 1 || e.single() !is ReferenceFunctionInstruction
                }

            if (writeInitializersAsExpressions)
                sink.writeVarUInt32(5u)
            else
                sink.writeVarUInt32(1u)

            context.serialize(sink, instance.type)

            sink.writeVarUInt32(instance.initializers.size.toUInt())
            instance.initializers.forEach { expression ->
                if (writeInitializersAsExpressions) {
                    context.serializeVector(sink, expression)
                } else {
                    val instruction = expression
                        .single() as ReferenceFunctionInstruction

                    sink.writeVarUInt32(instruction.functionIndex)
                }
            }
        }
    }
}
