package io.github.aljolen.components.elements.file

data class Block(
    val size: Int,
    var current: String = "_".repeat(size)
) {
    fun readAll(): String {
        return read(0, size)
    }

    fun read(from: Int, to: Int): String {
        check(from < to) { "Invalid range to read: $from .. $to" }
        return current.substring(from, to)
    }

    fun write(value: String, from: Int) {
        current = (read(0, from) + value + read(from, size)).substring(0, size)
    }
}
