package io.github.aljolen

import io.github.aljolen.utils.Link

fun main() {
    val fs = FileSystem(3, 4096 / 8)

    fs.mkdir(Link("test_dir"))
    fs.cd(Link("/"))
    fs.create(Link("readme.txt"))
    fs.link(Link("readme.txt"), Link("readme1.txt"))
    fs.symlink(Link("readme.txt"), Link("readme2.txt"))

    val text = "Hello World!"
    val iNode = fs.open(Link("./readme.txt"))

    fs.truncate(Link("./readme.txt"), text.length)
    fs.write(iNode, text.length, text)

    println(fs.read(iNode, text.length))

    fs.close(iNode)

    println(fs.ls(Link(".")))
    println(fs.stat(Link("readme1.txt")))
    println(fs.stat(Link("readme2.txt")))

    fs.unlink(Link("readme.txt"))
    fs.unlink(Link("readme1.txt"))
    fs.unlink(Link("readme2.txt"))
    fs.cd(Link(".."))
    fs.rmdir(Link("test_dir"))

    fs.symlink(Link("/"), Link("root"))
    println(fs.ls(Link(".")))
    fs.cd(Link("root"))
    println(fs.ls(Link(".")))
}