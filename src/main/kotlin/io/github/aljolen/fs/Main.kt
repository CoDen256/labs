package io.github.aljolen.fs

import io.github.aljolen.fs.console.Console

fun main() {
    val storage = MemoryStorage(16, 16)
    val io = FileIO(storage)
    val fs = DefaultFileSystem(io)
    val console = Console(fs, io, storage)


    fs.mkdir("a")
    fs.mkdir("a/b")
    fs.cd("a/b")
    fs.create("test.dat")
    fs.symlink("/a", "shortcut")
    fs.symlink("test.dat", "copy")

    console.run()
}