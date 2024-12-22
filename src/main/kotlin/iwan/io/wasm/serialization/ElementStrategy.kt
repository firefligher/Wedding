package dev.fir3.iwan.io.wasm.serialization

import dev.fir3.iwan.io.serialization.*
import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.DeserializationStrategy
import dev.fir3.iwan.io.serialization.SerializationContext
import dev.fir3.iwan.io.serialization.SerializationStrategy
import dev.fir3.iwan.io.serialization.deserialize
import dev.fir3.iwan.io.sink.ByteSink
import dev.fir3.iwan.io.sink.writeVarUInt32
import dev.fir3.iwan.io.source.ByteSource
import dev.fir3.iwan.io.source.expectInt8
import dev.fir3.iwan.io.source.readVarUInt32
import dev.fir3.iwan.io.wasm.models.*
import dev.fir3.iwan.io.wasm.models.instructions.ReferenceFunctionInstruction
import dev.fir3.iwan.io.wasm.models.valueTypes.ReferenceType
import java.io.IOException
import kotlin.math.exp

internal object ElementStrategy :
    DeserializationStrategy<Element>,
    SerializationStrategy<Element> {
    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext
    ) = when (val typeId = source.readVarUInt32()) {
        0u -> {
            val expression = context.deserialize<Expression>(source)
            val functionIndicesCount = source.readVarUInt32()
            val functionIndices = mutableListOf<UInt>()

            for (i in 0u until functionIndicesCount) {
                functionIndices.add(source.readVarUInt32())
            }

            ActiveElement(
                ReferenceType.FunctionReference,
                functionIndices.map { index ->
                    Expression(
                        listOf(ReferenceFunctionInstruction(index))
                    )
                },
                0u,
                expression
            )
        }

        1u -> {
            source.expectInt8(0)

            val functionIndicesCount = source.readVarUInt32()
            val functionIndices = mutableListOf<UInt>()

            for (i in 0u until functionIndicesCount) {
                functionIndices.add(source.readVarUInt32())
            }

            PassiveElement(
                ReferenceType.FunctionReference,
                functionIndices.map { index ->
                    Expression(
                        listOf(ReferenceFunctionInstruction(index))
                    )
                }
            )
        }

        2u -> {
            val tableIndex = source.readVarUInt32()
            val expression = context.deserialize<Expression>(source)
            source.expectInt8(0)
            val functionIndicesCount = source.readVarUInt32()
            val functionIndices = mutableListOf<UInt>()

            for (i in 0u until functionIndicesCount) {
                functionIndices.add(source.readVarUInt32())
            }

            ActiveElement(
                ReferenceType.FunctionReference,
                functionIndices.map { index ->
                    Expression(
                        listOf(ReferenceFunctionInstruction(index))
                    )
                },
                tableIndex,
                expression
            )
        }

        3u -> {
            source.expectInt8(0)

            val functionIndicesCount = source.readVarUInt32()
            val functionIndices = mutableListOf<UInt>()

            for (i in 0u until functionIndicesCount) {
                functionIndices.add(source.readVarUInt32())
            }

            DeclarativeElement(
                ReferenceType.FunctionReference,
                functionIndices.map { index ->
                    Expression(
                        listOf(ReferenceFunctionInstruction(index))
                    )
                }
            )
        }

        4u -> {
            val expression = context.deserialize<Expression>(source)
            val expressionsCount = source.readVarUInt32()
            val expressions = mutableListOf<Expression>()

            for (i in 0u until expressionsCount) {
                expressions.add(context.deserialize(source))
            }

            ActiveElement(
                ReferenceType.FunctionReference,
                expressions,
                0u,
                expression
            )
        }

        5u -> {
            val referenceType = context.deserialize<ReferenceType>(source)
            val expressionsCount = source.readVarUInt32()
            val expressions = mutableListOf<Expression>()

            for (i in 0u until expressionsCount) {
                expressions.add(context.deserialize(source))
            }

            PassiveElement(referenceType, expressions)
        }

        6u -> {
            val tableIndex = source.readVarUInt32()
            val expression = context.deserialize<Expression>(source)
            val referenceType = context.deserialize<ReferenceType>(source)
            val expressionsCount = source.readVarUInt32()
            val expressions = mutableListOf<Expression>()

            for (i in 0u until expressionsCount) {
                expressions.add(context.deserialize(source))
            }

            ActiveElement(
                referenceType,
                expressions,
                tableIndex,
                expression
            )
        }

        7u -> {
            val referenceType = context.deserialize<ReferenceType>(source)
            val expressionsCount = source.readVarUInt32()
            val expressions = mutableListOf<Expression>()

            for (i in 0u until expressionsCount) {
                expressions.add(context.deserialize(source))
            }

            DeclarativeElement(referenceType, expressions)
        }

        else -> throw IOException("Invalid element segment typeId '$typeId'")
    }

    override fun serialize(
        sink: ByteSink,
        context: SerializationContext,
        value: Element
    ) = when (value) {
        is ActiveElement -> {
            val writeTable = value.table > 0u
            val writeInitializersAsExpressions = value.initializers.any { e ->
                e.body.size > 1 ||
                        e.body.single() !is ReferenceFunctionInstruction
            }

            if (writeTable && writeInitializersAsExpressions)
                sink.writeVarUInt32(6u)
            else if (writeTable)
                sink.writeVarUInt32(2u)
            else if (writeInitializersAsExpressions)
                sink.writeVarUInt32(4u)
            else
                sink.writeVarUInt32(0u)

            if (writeTable) sink.writeVarUInt32(value.table)
            context.serialize(sink, value.offset)
            if (writeTable) context.serialize(sink, value.type)

            sink.writeVarUInt32(value.initializers.size.toUInt())
            value.initializers.forEach { expression ->
                if (writeInitializersAsExpressions) {
                    context.serialize(sink, expression)
                } else {
                    val instruction = expression
                        .body
                        .single() as ReferenceFunctionInstruction

                    sink.writeVarUInt32(instruction.functionIndex)
                }
            }
        }

        is DeclarativeElement -> {
            val writeInitializersAsExpressions = value.initializers.any { e ->
                e.body.size > 1 ||
                        e.body.single() !is ReferenceFunctionInstruction
            }

            if (writeInitializersAsExpressions)
                sink.writeVarUInt32(7u)
            else
                sink.writeVarUInt32(3u)

            context.serialize(sink, value.type)

            sink.writeVarUInt32(value.initializers.size.toUInt())
            value.initializers.forEach { expression ->
                if (writeInitializersAsExpressions) {
                    context.serialize(sink, expression)
                } else {
                    val instruction = expression
                        .body
                        .single() as ReferenceFunctionInstruction

                    sink.writeVarUInt32(instruction.functionIndex)
                }
            }
        }
        is PassiveElement -> {
            val writeInitializersAsExpressions = value.initializers.any { e ->
                e.body.size > 1 ||
                        e.body.single() !is ReferenceFunctionInstruction
            }

            if (writeInitializersAsExpressions)
                sink.writeVarUInt32(5u)
            else
                sink.writeVarUInt32(1u)

            context.serialize(sink, value.type)

            sink.writeVarUInt32(value.initializers.size.toUInt())
            value.initializers.forEach { expression ->
                if (writeInitializersAsExpressions) {
                    context.serialize(sink, expression)
                } else {
                    val instruction = expression
                        .body
                        .single() as ReferenceFunctionInstruction

                    sink.writeVarUInt32(instruction.functionIndex)
                }
            }
        }
    }
}
