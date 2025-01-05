package dev.fir3.wedding.linker.pool

import kotlin.reflect.KClass
import kotlin.reflect.cast

operator fun <TAnnotation : Annotation> Object.get(
    model: KClass<TAnnotation>
) = annotations[model]?.let(model::cast)

operator fun <TAnnotation : Annotation> Object.set(
    model: KClass<TAnnotation>,
    value: TAnnotation
) {
    annotations[model] = value
}
