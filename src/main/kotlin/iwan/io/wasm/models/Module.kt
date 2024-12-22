package dev.fir3.iwan.io.wasm.models

data class Module(
    val types: List<FunctionType>,
    val functions: List<Function>,
    val tables: List<TableType>,
    val memories: List<MemoryType>,
    val globals: List<Global>,
    val elements: List<Element>,
    val data: List<Data>,
    val imports: List<Import>,
    val exports: List<Export>
)
