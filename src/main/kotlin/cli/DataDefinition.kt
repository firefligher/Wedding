package dev.fir3.wedding.cli

import dev.fir3.wedding.linker.pool.ExportIdentifier

data class DataDefinition(
    val memory: ExportIdentifier,
    val address: UInt,
    val bytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataDefinition

        if (!bytes.contentEquals(other.bytes)) return false
        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + address.hashCode()
        return result
    }
}
