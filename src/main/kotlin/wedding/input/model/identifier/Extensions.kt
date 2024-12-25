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

// Function

internal val DefinedUnlinkedFunction.identifier: ExportedFunctionIdentifier?
    get() = exportName?.let { exportName ->
        ExportedFunctionIdentifier(
            function = exportName,
            module = module,
            type = type
        )
    }

internal val ImportedUnlinkedFunction.identifier: ImportedFunctionIdentifier
    get() = ImportedFunctionIdentifier(
        function = functionName,
        module = module,
        sourceModule = sourceModule,
        type = type
    )

internal val UnlinkedFunction.identifier: FunctionIdentifier?
    get() = when (this) {
        is DefinedUnlinkedFunction -> identifier
        is ImportedUnlinkedFunction -> identifier
    }

// Global

internal val DefinedUnlinkedGlobal.identifier: ExportedGlobalIdentifier?
    get() = exportName?.let { exportName ->
        ExportedGlobalIdentifier(
            global = exportName,
            module = module,
            type = type
        )
    }

internal val ImportedUnlinkedGlobal.identifier: ImportedGlobalIdentifier
    get() = ImportedGlobalIdentifier(
        global = globalName,
        module = module,
        sourceModule = sourceModule,
        type = type
    )

internal val UnlinkedGlobal.identifier: GlobalIdentifier? get() = when (this) {
    is DefinedUnlinkedGlobal -> identifier
    is ImportedUnlinkedGlobal -> identifier
}

// Memory

internal val DefinedUnlinkedMemory.identifier: ExportedMemoryIdentifier?
    get() = exportName?.let { exportName ->
        ExportedMemoryIdentifier(
            memory = exportName,
            module = module,
            type = type
        )
    }

internal val ImportedUnlinkedMemory.identifier: ImportedMemoryIdentifier
    get() = ImportedMemoryIdentifier(
        memory = memoryName,
        module = module,
        sourceModule = sourceModule,
        type = type
    )

internal val UnlinkedMemory.identifier: MemoryIdentifier? get() = when (this) {
    is DefinedUnlinkedMemory -> identifier
    is ImportedUnlinkedMemory -> identifier
}
