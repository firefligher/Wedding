package dev.fir3.wedding.wasm

data class Module(
    val codes: List<Code>,
    val datas: List<Data>,
    val elements: List<Element>,
    val exports: Collection<Export>,
    val functions: List<UInt>,
    val globals: List<Global>,
    val imports: List<Import>,
    val memories: List<Limits>,
    val startFunctionIndex: UInt?,
    val tables: List<TableType>,
    val types: List<FunctionType>
)
