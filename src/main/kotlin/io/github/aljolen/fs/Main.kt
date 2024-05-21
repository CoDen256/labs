package io.github.aljolen.fs

import io.github.aljolen.fs.console.Console

fun main() {
    val storage = MemoryStorage(16, 16)
    val fs = DefaultFileSystem(storage)
    val console = Console(fs, storage)


    fs.mkdir("a")
    fs.mkdir("a/b")
    fs.cd("a/b")
    fs.create("test.dat")

    console.run()
}