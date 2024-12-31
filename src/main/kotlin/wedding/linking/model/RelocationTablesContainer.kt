package dev.fir3.wedding.linking.model

internal interface RelocationTablesContainer {
    val dataRelocations: RelocationTable
    val functionRelocations: RelocationTable
    val globalRelocations: RelocationTable
    val memoryRelocations: RelocationTable
    val tableRelocations: RelocationTable
}
