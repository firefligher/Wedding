package dev.fir3.wedding.linker

import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.instructions.*

internal fun linkExpression(
    expression: Expression,
    moduleName: String,
    functionRelocations: RelocationTable,
    globalRelocations: RelocationTable
) = Expression(
    linkInstructions(
        expression.body,
        moduleName,
        functionRelocations,
        globalRelocations
    )
)

private fun linkInstructions(
    instructions: List<Instruction>,
    moduleName: String,
    functionRelocations: RelocationTable,
    globalRelocations: RelocationTable
): List<Instruction> = instructions.map { i ->
    when (i) {
        is AlignedMemoryInstruction -> i
        is BlockInstruction -> TODO()
        is ConditionalBlockInstruction -> ConditionalBlockInstruction(
            type = i.type,
            ifBody = linkInstructions(
                i.ifBody,
                moduleName,
                functionRelocations,
                globalRelocations
            ),
            elseBody = i.elseBody?.let {
                linkInstructions(
                    it,
                    moduleName,
                    functionRelocations,
                    globalRelocations
                )
            }
        )
        is LoopInstruction -> LoopInstruction(
            type = i.type,
            body = linkInstructions(
                i.body,
                moduleName,
                functionRelocations,
                globalRelocations
            )
        )
        is ConditionalBranchInstruction -> TODO()
        is TableBranchInstruction -> TODO()
        is UnconditionalBranchInstruction -> i
        is CallIndirectInstruction -> TODO()
        is CallInstruction -> CallInstruction(
            functionRelocations.resolveEntry(moduleName, i.functionIndex)
        )
        is ConstInstruction<*> -> i
        is FlatInstruction -> i
        is DataDropInstruction -> TODO()
        MemoryGrowInstruction -> TODO()
        is MemoryInitInstruction -> TODO()
        MemorySizeInstruction -> TODO()
        is ReferenceFunctionInstruction -> TODO()
        is TableInstruction -> TODO()
        is GlobalGetInstruction -> GlobalGetInstruction(
            globalRelocations.resolveEntry(moduleName, i.globalIndex)
        )
        is GlobalSetInstruction -> GlobalSetInstruction(
            globalRelocations.resolveEntry(moduleName, i.globalIndex)
        )
        is LocalGetInstruction -> i
        is LocalSetInstruction -> i
        is LocalTeeInstruction -> i
    }
}
