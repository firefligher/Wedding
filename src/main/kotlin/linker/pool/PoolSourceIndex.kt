package dev.fir3.wedding.linker.pool

data class PoolSourceIndex(
    val datas: Map<Pair<String, UInt>, Data>,
    val elements: Map<Pair<String, UInt>, Element>,
    val functions: Map<Pair<String, UInt>, Function>,
    val functionTypes: Map<Pair<String, UInt>, FunctionType>,
    val globals: Map<Pair<String, UInt>, Global>,
    val memories: Map<Pair<String, UInt>, Memory>,
    val tables: Map<Pair<String, UInt>, Table>
)
