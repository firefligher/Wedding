package dev.fir3.wedding.input.model.identifier

internal sealed interface Identifier<TType> {
    val type: TType
}
