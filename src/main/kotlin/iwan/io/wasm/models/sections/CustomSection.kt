package dev.fir3.iwan.io.wasm.models.sections

data class CustomSection(
    val name: String,
    val content: ByteArray
): Section {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomSection

        if (name != other.name) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}
