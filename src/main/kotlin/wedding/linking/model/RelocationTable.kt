package dev.fir3.wedding.linking.model

interface RelocationTable {
    operator fun get(module: String, originalIndex: UInt): UInt?
}
