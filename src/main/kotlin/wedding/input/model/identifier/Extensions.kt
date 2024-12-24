package dev.fir3.wedding.input.model.identifier

import dev.fir3.wedding.input.model.function.DefinedUnlinkedFunction
import dev.fir3.wedding.input.model.function.ImportedUnlinkedFunction
import dev.fir3.wedding.input.model.function.UnlinkedFunction
import dev.fir3.wedding.input.model.global.DefinedUnlinkedGlobal
import dev.fir3.wedding.input.model.global.ImportedUnlinkedGlobal
import dev.fir3.wedding.input.model.global.UnlinkedGlobal
import dev.fir3.wedding.input.model.memory.DefinedUnlinkedMemory
import dev.fir3.wedding.input.model.memory.ImportedUnlinkedMemory
import dev.fir3.wedding.input.model.memory.UnlinkedMemory

internal val UnlinkedFunction.identifier: Identifier? get() = when (this) {
    is DefinedUnlinkedFunction -> exportName?.let { exportName ->
        ExportedFunctionIdentifier(
            function = exportName,
            module = module,
            type = type
        )
    }

    is ImportedUnlinkedFunction -> ImportedFunctionIdentifier(
        function = functionName,
        module = module,
        sourceModule = sourceModule,
        type = type
    )
}

internal val UnlinkedGlobal.identifier: Identifier? get() = when (this) {
    is DefinedUnlinkedGlobal -> exportName?.let { exportName ->
        ExportedGlobalIdentifier(
            global = exportName,
            module = module,
            type = type
        )
    }

    is ImportedUnlinkedGlobal -> ImportedGlobalIdentifier(
        global = globalName,
        module = module,
        sourceModule = sourceModule,
        type = type
    )
}

internal val UnlinkedMemory.identifier: Identifier? get() = when (this) {
    is DefinedUnlinkedMemory -> exportName?.let { exportName ->
        ExportedMemoryIdentifier(
            memory = exportName,
            module = module,
            type = type
        )
    }

    is ImportedUnlinkedMemory -> ImportedMemoryIdentifier(
        memory = memoryName,
        module = module,
        sourceModule = sourceModule,
        type = type
    )
}
