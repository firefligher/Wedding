package dev.fir3.wedding.linker.pool

data class Pool(
    val datas: MutableList<Data> = mutableListOf(),
    val elements: MutableList<Element> = mutableListOf(),
    val functions: MutableList<Function> = mutableListOf(),
    val functionTypes: MutableList<FunctionType> = mutableListOf(),
    val globals: MutableList<Global> = mutableListOf(),
    val memories: MutableList<Memory> = mutableListOf(),
    val tables: MutableList<Table> = mutableListOf()
) {
    fun add(`object`: Object) = when (`object`) {
        is Data -> datas += `object`
        is Element -> elements += `object`
        is Function -> functions += `object`
        is FunctionType -> functionTypes += `object`
        is Global -> globals += `object`
        is Memory -> memories += `object`
        is Table -> tables += `object`
    }

    fun remove(`object`: Object) = when (`object`) {
        is Data -> datas -= `object`
        is Element -> elements -= `object`
        is Function -> functions -= `object`
        is FunctionType -> functionTypes -= `object`
        is Global -> globals -= `object`
        is Memory -> memories -= `object`
        is Table -> tables -= `object`
    }
}
