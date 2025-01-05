package dev.fir3.wedding.wasm

sealed interface Data {
    val initializers: ByteArray
}

data class ActiveData(
    override val initializers: ByteArray,
    val memoryIndex: UInt,
    val offset: List<Instruction>
): Data {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ActiveData

        if (!initializers.contentEquals(other.initializers)) return false
        if (memoryIndex != other.memoryIndex) return false
        if (offset != other.offset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = initializers.contentHashCode()
        result = 31 * result + memoryIndex.hashCode()
        result = 31 * result + offset.hashCode()
        return result
    }
}

data class PassiveData(override val initializers: ByteArray): Data {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PassiveData

        return initializers.contentEquals(other.initializers)
    }

    override fun hashCode(): Int {
        return initializers.contentHashCode()
    }
}
