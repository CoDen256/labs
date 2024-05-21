package io.github.aljolen.fs

import io.github.aljolen.fs.console.Console

fun main() {
    val storage = MemoryStorage(16, 16)
    val console = Console(DefaultFileSystem(storage), storage)
    console.run()
}