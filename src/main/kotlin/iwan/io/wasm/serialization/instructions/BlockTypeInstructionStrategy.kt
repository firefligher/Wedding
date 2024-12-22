package dev.fir3.iwan.io.wasm.serialization.instructions

import dev.fir3.iwan.io.serialization.DeserializationContext
import dev.fir3.iwan.io.serialization.deserialize
import dev.fir3.iwan.io.source.*
import dev.fir3.iwan.io.wasm.models.instructions.*
import dev.fir3.iwan.io.wasm.models.instructions.blockTypes.BlockType
import java.io.IOException
import kotlin.reflect.KClass

internal object BlockTypeInstructionStrategy :
    InstructionDeserializationStrategy {
    @Throws(IOException::class)
    private fun <TBlockTypeInstruction : BlockTypeInstruction> build(
        source: ByteSource,
        context: DeserializationContext,
        isElseAllowed: Boolean,
        factory: (
            BlockType,
            List<Instruction>,
            List<Instruction>?
        ) -> TBlockTypeInstruction
    ): TBlockTypeInstruction {
        val blockType = context.deserialize<BlockType>(source)
        val ifBody = mutableListOf<Instruction>()
        val hasElseBody: Boolean

        while (true) {
            val nextByte = source.peekUInt8()
            val isTerminated = nextByte == InstructionIds.END
                    || nextByte == InstructionIds.ELSE

            if (isTerminated) {
                hasElseBody = nextByte == InstructionIds.ELSE
                break
            }

            ifBody.add(context.deserialize(source))
        }

        source.expectUInt8(
            if (hasElseBody) InstructionIds.ELSE
            else InstructionIds.END
        )

        val elseBody = if (isElseAllowed && hasElseBody) {
            val list = mutableListOf<Instruction>()

            while (source.peekUInt8() != InstructionIds.END) {
                list.add(context.deserialize(source))
            }

            source.expectUInt8(InstructionIds.END)
            list
        } else {
            null
        }

        return factory(blockType, ifBody, elseBody)
    }

    @Throws(IOException::class)
    override fun deserialize(
        source: ByteSource,
        context: DeserializationContext,
        model: KClass<out Instruction>,
        instance: Instruction?
    ) = when (model) {
        BlockInstruction::class -> build(
            source,
            context,
            false
        ) { blockType, body, _ -> BlockInstruction(blockType, body) }

        LoopInstruction::class -> build(
            source,
            context,
            false
        ) { blockType, body, _ -> LoopInstruction(blockType, body) }

        ConditionalBlockInstruction::class -> build(
            source,
            context,
            true
        ) { blockType, ifBody, elseBody ->
            ConditionalBlockInstruction(blockType, ifBody, elseBody)
        }

        else -> throw IOException("Invalid block type: $model")
    }
}