package dev.fir3.wedding.linker

import dev.fir3.wedding.input.function.ExportedFunction
import dev.fir3.wedding.input.function.ImportedFunction
import dev.fir3.wedding.input.function.InternalFunction
import dev.fir3.wedding.input.global.ExportedGlobal
import dev.fir3.wedding.input.global.ImportedGlobal
import dev.fir3.wedding.input.global.InternalGlobal
import dev.fir3.wedding.input.memory.ExportedMemory
import dev.fir3.wedding.input.memory.ImportedMemory
import dev.fir3.wedding.input.memory.InternalMemory
import dev.fir3.wedding.linker.function.LinkedExportedFunction
import dev.fir3.wedding.linker.function.LinkedImportedFunction
import dev.fir3.wedding.linker.function.LinkedInternalFunction

internal val functionLinker = IndexedLinker(
    ExportedFunction::class,
    ImportedFunction::class,
    { import, export ->
        import.sourceModule == export.moduleName &&
                import.functionName == export.functionName
    },
    { function, relocatedIndex, moduleName ->
        when (function) {
            is ExportedFunction -> LinkedExportedFunction(
                expression = function.expression,
                functionName = function.moduleName
                        + '$'
                        + function.functionName,

                index = relocatedIndex,
                isStart = function.isStart,
                locals = function.locals,
                moduleName = moduleName,
                originalModule = function.moduleName,
                type = function.type
            )

            is ImportedFunction -> LinkedImportedFunction(
                functionName = function.functionName,
                index = relocatedIndex,
                isStart = function.isStart,
                moduleName = moduleName,
                originalModule = function.moduleName,
                sourceModule = function.sourceModule,
                type = function.type
            )

            is InternalFunction -> LinkedInternalFunction(
                expression = function.expression,
                index = relocatedIndex,
                isStart = function.isStart,
                locals = function.locals,
                moduleName = moduleName,
                originalModule = function.moduleName,
                type = function.type
            )
        }
    },
    { a, b ->
        a is LinkedImportedFunction &&
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
        a is ImportedGlobal &&
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
        a is ImportedMemory &&
                a.sourceModule == b.sourceModule &&
                a.memoryName == b.memoryName
    }
)
