package dev.fir3.wedding.linking

import dev.fir3.iwan.io.wasm.models.Expression
import dev.fir3.iwan.io.wasm.models.instructions.*
import dev.fir3.wedding.linking.model.MutableRelocationContainer
import dev.fir3.wedding.linking.model.RelocationTablesContainer
import dev.fir3.wedding.linking.model.data.ActiveRelocatedData
import dev.fir3.wedding.linking.model.data.PassiveRelocatedData
import dev.fir3.wedding.linking.model.element.ActiveRelocatedElement
import dev.fir3.wedding.linking.model.element.DeclarativeRelocatedElement
import dev.fir3.wedding.linking.model.element.PassiveRelocatedElement
import dev.fir3.wedding.linking.model.function.DefinedRelocatedFunction
import dev.fir3.wedding.linking.model.function.ImportedRelocatedFunction
import dev.fir3.wedding.linking.model.global.DefinedRelocatedGlobal
import dev.fir3.wedding.linking.model.global.ImportedRelocatedGlobal

internal fun fixInstructions(container: MutableRelocationContainer) {
    // Data

    val fixedDatas = container.datas.map { data ->
        when (data) {
            is ActiveRelocatedData -> data.copy(
                offset = fixExpression(
                    data.offset,
                    data.originalModule,
                    container
                )
            )
            is PassiveRelocatedData -> data
        }
    }

    container.datas.clear()
    container.datas.addAll(fixedDatas)

    // Elements

    val fixedElements = container.elements.map { element ->
        when (element) {
            is ActiveRelocatedElement -> element.copy(
                initializers = element.initializers.map { initializer ->
                    fixExpression(
                        initializer,
                        element.originalModule,
                        container
                    )
                },
                offset = fixExpression(
                    element.offset,
                    element.originalModule,
                    container
                )
            )
            is DeclarativeRelocatedElement -> element.copy(
                initializers = element.initializers.map { initializer ->
                    fixExpression(
                        initializer,
                        element.originalModule,
                        container
                    )
                }
            )
            is PassiveRelocatedElement -> element.copy(
                initializers = element.initializers.map { initializer ->
                    fixExpression(
                        initializer,
                        element.originalModule,
                        container
                    )
                }
            )
        }
    }

    container.elements.clear()
    container.elements.addAll(fixedElements)

    // Functions

    val fixedFunctions = container.functions.map { function ->
        when (function) {
            is DefinedRelocatedFunction -> function.copy(
                expression = fixExpression(
                    function.expression,
                    function.originalModule,
                    container
                )
            )

            is ImportedRelocatedFunction -> function
        }
    }

    container.functions.clear()
    container.functions.addAll(fixedFunctions)

    // Globals

    val fixedGlobals = container.globals.map { global ->
        when (global) {
            is DefinedRelocatedGlobal -> global.copy(
                initializer = fixExpression(
                    global.initializer,
                    global.originalModule,
                    container
                )
            )
            is ImportedRelocatedGlobal -> global
        }
    }

    container.globals.clear()
    container.globals.addAll(fixedGlobals)
}

private fun fixExpression(
    expression: Expression,
    originalModule: String,
    tables: RelocationTablesContainer
) = Expression(
    expression.body.map { i -> fixInstruction(i, originalModule, tables) }
)

private fun fixInstruction(
    instruction: Instruction,
    originalModule: String,
    tables: RelocationTablesContainer
): Instruction = when (instruction) {
    is AlignedMemoryInstruction -> instruction
    is BlockInstruction -> TODO()
    is ConditionalBlockInstruction -> ConditionalBlockInstruction(
        type = instruction.type,
        ifBody = instruction.ifBody.map { i ->
            fixInstruction(i, originalModule, tables)
        },
        elseBody = instruction.elseBody?.map { i ->
            fixInstruction(i, originalModule, tables)
        }
    )
    is LoopInstruction -> LoopInstruction(
        type = instruction.type,
        body = instruction.body.map { i ->
            fixInstruction(i, originalModule, tables)
        }
    )
    is ConditionalBranchInstruction -> TODO()
    is TableBranchInstruction -> TODO()
    is UnconditionalBranchInstruction -> instruction
    is CallIndirectInstruction -> TODO()
    is CallInstruction -> CallInstruction(
        checkNotNull(
            tables.functionRelocations[
                originalModule,
                instruction.functionIndex
            ]
        )
    )
    is ConstInstruction<*> -> instruction
    is FlatInstruction -> instruction
    is DataDropInstruction -> TODO()
    MemoryGrowInstruction -> TODO()
    is MemoryInitInstruction -> TODO()
    MemorySizeInstruction -> TODO()
    is ReferenceFunctionInstruction -> TODO()
    is TableInstruction -> TODO()
    is GlobalGetInstruction -> GlobalGetInstruction(
        checkNotNull(
            tables.globalRelocations[originalModule, instruction.globalIndex]
        )
    )
    is GlobalSetInstruction -> GlobalSetInstruction(
        checkNotNull(
            tables.globalRelocations[originalModule, instruction.globalIndex]
        )
    )
    is LocalGetInstruction -> instruction
    is LocalSetInstruction -> instruction
    is LocalTeeInstruction -> instruction
}
