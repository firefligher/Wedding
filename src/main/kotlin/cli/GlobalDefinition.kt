package dev.fir3.wedding.cli

data class GlobalDefinition(
    val initialValue: Number,
    val isMutable: Boolean,
    val name: String,
    val type: StorableType
)
