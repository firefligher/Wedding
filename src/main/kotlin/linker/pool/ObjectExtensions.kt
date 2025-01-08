package dev.fir3.wedding.linker.pool

import kotlin.reflect.KClass
import kotlin.reflect.cast

operator fun <TAnnotation : Annotation> Object.get(
    model: KClass<TAnnotation>
) = annotations[model]?.let(model::cast)

fun Object.isImportCompatibleWith(
    `object`: Object,
    pool: Pool
): Boolean {
    when (this) {
        is Function -> {
            if (`object` !is Function) return false

            val ourModule = this[SourceModule::class]!!.name
            val ourTypeIndex = this[FunctionTypeIndex::class]!!.typeIndex
            val theirModule = `object`[SourceModule::class]!!.name
            val theirTypeIndex = `object`[FunctionTypeIndex::class]!!.typeIndex

            val ourTypeObject = pool.resolve(
                IndexIdentifier(
                    index = ourTypeIndex,
                    module = ourModule,
                    type = ObjectType.FUNCTION_TYPE
                )
            )

            val theirTypeObject = pool.resolve(
                IndexIdentifier(
                    index = theirTypeIndex,
                    module = theirModule,
                    type = ObjectType.FUNCTION_TYPE
                )
            )

            val ourType = ourTypeObject!![FunctionTypeInfo::class]!!.type
            val theirType = theirTypeObject!![FunctionTypeInfo::class]!!.type

            return ourType == theirType
        }

        is Global -> {
            if (`object` !is Global) return false

            val ourTypeInfo = this[GlobalType::class]!!
            val theirTypeInfo = `object`[GlobalType::class]!!

            if (ourTypeInfo.isMutable && !theirTypeInfo.isMutable) return false

            return ourTypeInfo.type == theirTypeInfo.type
        }

        is Memory -> {
            if (`object` !is Memory) return false

            val ourLimits = this[MemoryInfo::class]!!.limits
            val theirLimits = `object`[MemoryInfo::class]!!.limits

            // TODO: This is just workaround. Find a better solution.

            return true
        }
        is Table -> {
            if (`object` !is Table) return false

            val ourType = this[TableInfo::class]!!.type
            val theirType = `object`[TableInfo::class]!!.type

            return ourType == theirType
        }
        else -> return false
    }
}

operator fun <TAnnotation : Annotation> Object.set(
    model: KClass<TAnnotation>,
    value: TAnnotation
) {
    annotations[model] = value
}
