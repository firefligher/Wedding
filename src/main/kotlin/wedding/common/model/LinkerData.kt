package dev.fir3.wedding.common.model

internal interface LinkerData : Indexed {
    val initializers: List<Byte>
}
