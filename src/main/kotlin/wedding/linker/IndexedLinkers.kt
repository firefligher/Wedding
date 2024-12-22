package dev.fir3.wedding.relocation

import dev.fir3.wedding.input.function.ExportedFunction
import dev.fir3.wedding.input.function.ImportedFunction
import dev.fir3.wedding.input.function.InternalFunction
import dev.fir3.wedding.input.global.ExportedGlobal
import dev.fir3.wedding.input.global.ImportedGlobal
import dev.fir3.wedding.input.global.InternalGlobal
import dev.fir3.wedding.input.memory.ExportedMemory
import dev.fir3.wedding.input.memory.ImportedMemory
import dev.fir3.wedding.input.memory.InternalMemory

internal val functionLinker = IndexedLinker(
    ExportedFunction::class,
    ImportedFunction::class,
    { import, export ->
        import.sourceModule == export.moduleName &&
                import.functionName == export.functionName
    },
    { function, relocatedIndex, moduleName ->
        when (function) {
            is ExportedFunction -> function.copy(
                index = relocatedIndex,
                moduleName = moduleName,
                functionName = function.moduleName
                        + '$'
                        + function.functionName
            )

            is ImportedFunction -> function.copy(
                index = relocatedIndex,
                moduleName = moduleName
            )

            is InternalFunction -> function.copy(
                index = relocatedIndex,
                moduleName = moduleName
            )
        }
    },
    { a, b ->
        a.sourceModule == b.sourceModule &&
                a.functionName == b.functionName
    }
)

internal val globalLinker = IndexedLinker(
    ExportedGlobal::class,
    ImportedGlobal::class,
    { import, export ->
        import.sourceModule == export.moduleName &&
                import.globalName == export.globalName
    },
    { global, relocatedIndex, moduleName ->
        when (global) {
            is ExportedGlobal -> global.copy(
                index = relocatedIndex,
                moduleName = moduleName,
                globalName = global.moduleName + '$' + global.globalName
            )

            is ImportedGlobal -> global.copy(
                index = relocatedIndex,
                moduleName = moduleName
            )

            is InternalGlobal -> global.copy(
                index = relocatedIndex,
                moduleName = moduleName
            )
        }
    },
    { a, b ->
        a.sourceModule == b.sourceModule &&
                a.globalName == b.globalName
    }
)

internal val memoryLinker = IndexedLinker(
    ExportedMemory::class,
    ImportedMemory::class,
    { import, export ->
        import.sourceModule == export.moduleName &&
                import.memoryName == export.memoryName
    },
    { memory, relocatedIndex, moduleName ->
        when (memory) {
            is ExportedMemory -> memory.copy(
                index = relocatedIndex,
                moduleName = moduleName,
                memoryName = memory.moduleName + '$' + memory.memoryName
            )

            is ImportedMemory -> memory.copy(
                index = relocatedIndex,
                moduleName = moduleName
            )

            is InternalMemory -> memory.copy(
                index = relocatedIndex,
                moduleName = moduleName
            )
        }
    },
    { a, b ->
        a.sourceModule == b.sourceModule &&
                a.memoryName == b.memoryName
    }
)
