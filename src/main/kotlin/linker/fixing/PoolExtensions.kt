package dev.fir3.wedding.linker.fixing

import dev.fir3.wedding.linker.pool.*
import dev.fir3.wedding.wasm.*

fun Pool.fixDatas(sourceModule: String, poolSourceIndex: PoolSourceIndex) {
    for (data in datas) {
        val activeDataOffset = data[ActiveDataInfo::class] ?: continue

        data[FixedActiveDataInfo::class] = FixedActiveDataInfo(
            memoryIndex = poolSourceIndex.resolveDataIndex(
                sourceModule,
                activeDataOffset.memoryIndex
            ),
            offset = fixInstructions(
                activeDataOffset.offset,
                sourceModule,
                poolSourceIndex
            )
        )
    }
}

fun Pool.fixElements(sourceModule: String, poolSourceIndex: PoolSourceIndex) {
    for (element in elements) {
        val initializers = element[ElementInfo::class]!!.initializers

        element[FixedElementInitializers::class] = FixedElementInitializers(
            initializers.map { initializer ->
                fixInstructions(initializer, sourceModule, poolSourceIndex)
            }
        )

        val activeElementInfo = element[ActiveElementInfo::class] ?: continue

        element[FixedActiveElementInfo::class] = FixedActiveElementInfo(
            offset = fixInstructions(
                activeElementInfo.offset,
                sourceModule,
                poolSourceIndex
            ),
            tableIndex = poolSourceIndex.resolveTableIndex(
                sourceModule,
                activeElementInfo.tableIndex
            )
        )
    }
}

fun Pool.fixFunctions(sourceModule: String, poolSourceIndex: PoolSourceIndex) {
    for (function in functions) {
        val typeIndex = function[FunctionTypeIndex::class]!!.typeIndex

        function[FixedFunctionTypeIndex::class] = FixedFunctionTypeIndex(
            poolSourceIndex.resolveFunctionTypeIndex(
                sourceModule,
                typeIndex
            )
        )

        val code = function[FunctionBody::class]?.code ?: continue

        function[FixedFunctionBody::class] = FixedFunctionBody(
            code.copy(
                body = fixInstructions(
                    code.body,
                    sourceModule,
                    poolSourceIndex
                )
            )
        )
    }
}

fun Pool.fixGlobals(sourceModule: String, poolSourceIndex: PoolSourceIndex) {
    for (global in globals) {
        val initializer = global[GlobalInitializer::class]?.instructions
            ?: continue

        global[FixedGlobalInitializer::class] = FixedGlobalInitializer(
            instructions = fixInstructions(
                initializer,
                sourceModule,
                poolSourceIndex
            )
        )
    }
}

private fun fixInstructions(
    instructions: List<Instruction>,
    sourceModule: String,
    poolSourceIndex: PoolSourceIndex
) = instructions.map { instruction ->
    fixInstruction(instruction, sourceModule, poolSourceIndex)
}

private fun fixInstruction(
    instruction: Instruction,
    sourceModule: String,
    poolSourceIndex: PoolSourceIndex
): Instruction = when (instruction) {
    is AlignedMemoryInstruction -> instruction
    is BlockInstruction -> BlockInstruction(
        type = fixBlockType(instruction.type, sourceModule, poolSourceIndex),
        body = fixInstructions(instruction.body, sourceModule, poolSourceIndex)
    )

    is ConstInstruction<*> -> instruction
    is ConditionalBlockInstruction -> ConditionalBlockInstruction(
        type = fixBlockType(instruction.type, sourceModule, poolSourceIndex),
        ifBody = fixInstructions(
            instruction.ifBody,
            sourceModule,
            poolSourceIndex
        ),
        elseBody = instruction.elseBody?.let { elseBody ->
            fixInstructions(elseBody, sourceModule, poolSourceIndex)
        }
    )

    is LoopInstruction -> LoopInstruction(
        type = fixBlockType(instruction.type, sourceModule, poolSourceIndex),
        body = fixInstructions(instruction.body, sourceModule, poolSourceIndex)
    )
    is CallIndirectInstruction -> CallIndirectInstruction(
        typeIndex = poolSourceIndex.resolveFunctionTypeIndex(
            sourceModule,
            instruction.typeIndex
        ),
        tableIndex = poolSourceIndex.resolveTableIndex(
            sourceModule,
            instruction.tableIndex
        )
    )
    is CallInstruction -> CallInstruction(
        functionIndex = poolSourceIndex.resolveFunctionIndex(
            sourceModule,
            instruction.functionIndex
        )
    )

    is ConditionalBranchInstruction -> instruction
    is DataDropInstruction -> DataDropInstruction(
        dataIndex = poolSourceIndex.resolveDataIndex(
            sourceModule,
            instruction.dataIndex
        )
    )

    is ElementDropInstruction -> ElementDropInstruction(
        elementIndex = poolSourceIndex.resolveElementIndex(
            sourceModule,
            instruction.elementIndex
        )
    )

    is GlobalGetInstruction -> GlobalGetInstruction(
        globalIndex = poolSourceIndex.resolveGlobalIndex(
            sourceModule,
            instruction.globalIndex
        )
    )

    is GlobalSetInstruction -> GlobalSetInstruction(
        globalIndex = poolSourceIndex.resolveGlobalIndex(
            sourceModule,
            instruction.globalIndex
        )
    )

    is LocalGetInstruction -> instruction
    is LocalSetInstruction -> instruction
    is LocalTeeInstruction -> instruction
    MemoryGrowInstruction -> instruction
    is MemoryInitInstruction -> MemoryInitInstruction(
        dataIndex = poolSourceIndex.resolveDataIndex(
            sourceModule,
            instruction.dataIndex
        )
    )

    MemorySizeInstruction -> instruction
    is NonParameterizedInstructions -> instruction
    is ReferenceFunctionInstruction -> ReferenceFunctionInstruction(
        functionIndex = poolSourceIndex.resolveFunctionTypeIndex(
            sourceModule,
            instruction.functionIndex
        )
    )

    is TableBranchInstruction -> instruction
    is TableCopyInstruction -> TableCopyInstruction(
        tableIndex1 = poolSourceIndex.resolveTableIndex(
            sourceModule,
            instruction.tableIndex1
        ),
        tableIndex2 = poolSourceIndex.resolveTableIndex(
            sourceModule,
            instruction.tableIndex2
        )
    )

    is TableFillInstruction -> TableFillInstruction(
        tableIndex = poolSourceIndex.resolveTableIndex(
            sourceModule,
            instruction.tableIndex
        )
    )

    is TableGetInstruction -> TableGetInstruction(
        tableIndex = poolSourceIndex.resolveTableIndex(
            sourceModule,
            instruction.tableIndex
        )
    )

    is TableGrowInstruction -> TableGrowInstruction(
        tableIndex = poolSourceIndex.resolveTableIndex(
            sourceModule,
            instruction.tableIndex
        )
    )

    is TableInitInstruction -> TableInitInstruction(
        tableIndex = poolSourceIndex.resolveTableIndex(
            sourceModule,
            instruction.tableIndex
        ),
        elementIndex = poolSourceIndex.resolveElementIndex(
            sourceModule,
            instruction.elementIndex
        )
    )

    is TableSetInstruction -> TableSetInstruction(
        tableIndex = poolSourceIndex.resolveTableIndex(
            sourceModule,
            instruction.tableIndex
        )
    )

    is TableSizeInstruction -> TableSizeInstruction(
        tableIndex = poolSourceIndex.resolveTableIndex(
            sourceModule,
            instruction.tableIndex
        )
    )

    is UnconditionalBranchInstruction -> instruction
}

private fun fixBlockType(
    blockType: BlockType,
    sourceModule: String,
    poolSourceIndex: PoolSourceIndex
) = when (blockType) {
    is FunctionBlockType -> FunctionBlockType(
        value = poolSourceIndex.resolveFunctionTypeIndex(
            sourceModule = sourceModule,
            sourceIndex = blockType.value
        )
    )
    else -> blockType
}
