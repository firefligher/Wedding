package dev.fir3.wedding.linker.pool

import kotlin.reflect.KClass

sealed interface Object {
    val annotations: MutableMap<KClass<out Annotation>, Annotation>
    val identifier: Identifier
}

data class Data(
    override val annotations: MutableMap<KClass<out Annotation>, Annotation> =
        mutableMapOf()
) : Object {
    override val identifier: Identifier get() =
        makeIdentifier(ObjectType.DATA, annotations)
}

data class Element(
    override val annotations: MutableMap<KClass<out Annotation>, Annotation> =
        mutableMapOf()
) : Object {
    override val identifier: Identifier get() =
        makeIdentifier(ObjectType.ELEMENT, annotations)
}

data class Function(
    override val annotations: MutableMap<KClass<out Annotation>, Annotation> =
        mutableMapOf()
) : Object {
    override val identifier: Identifier get() =
        makeIdentifier(ObjectType.FUNCTION, annotations)
}

data class FunctionType(
    override val annotations: MutableMap<KClass<out Annotation>, Annotation> =
        mutableMapOf()
) : Object {
    override val identifier: Identifier get() =
        makeIdentifier(ObjectType.FUNCTION_TYPE, annotations)
}

data class Global(
    override val annotations: MutableMap<KClass<out Annotation>, Annotation> =
        mutableMapOf()
) : Object {
    override val identifier: Identifier get() =
        makeIdentifier(ObjectType.GLOBAL, annotations)
}

data class Memory(
    override val annotations: MutableMap<KClass<out Annotation>, Annotation> =
        mutableMapOf()
) : Object {
    override val identifier: Identifier get() =
        makeIdentifier(ObjectType.MEMORY, annotations)
}

data class Table(
    override val annotations: MutableMap<KClass<out Annotation>, Annotation> =
        mutableMapOf()
) : Object {
    override val identifier: Identifier get() =
        makeIdentifier(ObjectType.TABLE, annotations)
}
