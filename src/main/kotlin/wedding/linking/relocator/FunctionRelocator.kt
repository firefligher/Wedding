package dev.fir3.wedding.linking.relocator

import dev.fir3.wedding.input.model.function.DefinedUnlinkedFunction
import dev.fir3.wedding.input.model.function.ImportedUnlinkedFunction
import dev.fir3.wedding.input.model.function.UnlinkedFunction
import dev.fir3.wedding.linking.model.function.DefinedRelocatedFunction
import dev.fir3.wedding.linking.model.function.ImportedRelocatedFunction
import dev.fir3.wedding.linking.model.function.RelocatedFunction

internal object FunctionRelocator : AbstractExportableRelocator<
        UnlinkedFunction,
        DefinedUnlinkedFunction,
        ImportedUnlinkedFunction,
        RelocatedFunction
>(DefinedUnlinkedFunction::class, ImportedUnlinkedFunction::class) {
    override fun deriveOutputElement(
        input: UnlinkedFunction,
        outputModuleName: String,
        relocatedIndex: UInt
    ) = when (input) {
        is DefinedUnlinkedFunction -> DefinedRelocatedFunction(
            exportName = input.exportName,
            expression = input.expression,
            isStart = input.isStart,
            type = input.type,
            index = relocatedIndex,
            locals = input.locals,
            module = outputModuleName,
            originalModule = input.module
        )
        is ImportedUnlinkedFunction -> ImportedRelocatedFunction(
            exportName = input.exportName,
            functionName = input.functionName,
            isStart = input.isStart,
            type = input.type,
            index = relocatedIndex,
            module = outputModuleName,
            originalModule = input.module,
            sourceModule = input.sourceModule
        )
    }

    override fun isLinkable(
        import: ImportedUnlinkedFunction,
        definition: DefinedUnlinkedFunction
    ) = import.sourceModule == definition.module &&
            import.functionName == definition.exportName
}
