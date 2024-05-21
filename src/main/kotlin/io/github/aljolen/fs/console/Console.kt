package io.github.aljolen.fs.console

import io.github.aljolen.fs.api.*
import io.github.aljolen.fs.utils.StorageDisplay

class Console(private val fs: FS, private val storage: Storage) {

    private val display = StorageDisplay()

    fun run() {
        while (true) {
            print("${fs.cwd()}> ")
            val input = readln()
            try {
                val cmd = create(input)
                val result = handle(cmd) ?: break
                output(result)
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    fun create(input: String): Command {
        val args = input.split(" ")
        return when (args[0]) {
            "stat" -> Stat(args[1])
            "ls" -> Ls
            "create" -> Create(args[1])
            "open" -> Open(args[1])
            "close" -> Close(args[1].toInt())
            "seek" -> Seek(args[1].toInt(), args[2].toInt())
            "read" -> Read(args[1].toInt(), args[2].toInt())
            "write" -> Write(
                args[1].toInt(),
                args[2].toInt(),
                args.subList(3, args.size).joinToString(" ").toByteArray()
            )

            "truncate" -> Truncate(args[1], args[2].toInt())
            "link" -> Link(args[1], args[2])
            "unlink" -> Unlink(args[1])
            "mkdir" -> Mkdir(args[1])
            "rmdir" -> Rmdir(args[1])
            "cd" -> Cd(args[1])
            "symlink" -> Symlink(args[1], args[2])
            "exit" -> Exit
            "show" -> ShowStorage
            else -> Unknown
        }
    }

    fun handle(cmd: Command): Any? = when (cmd) {
        is Stat -> fs.stat(cmd.pathname)
        is Ls -> fs.ls()
        is Create -> fs.create(cmd.pathname)
        is Open -> fs.open(cmd.pathname)
        is Close -> fs.close(cmd.fd)
        is Seek -> fs.seek(cmd.fd, cmd.offset)
        is Read -> fs.read(cmd.fd, cmd.size)
        is Write -> fs.write(cmd.fd, cmd.size, cmd.content)
        is Truncate -> fs.truncate(cmd.pathname, cmd.size)
        is Link -> fs.link(cmd.pathname1, cmd.pathname2)
        is Unlink -> fs.unlink(cmd.pathname)
        is Mkdir -> fs.mkdir(cmd.pathname)
        is Rmdir -> fs.rmdir(cmd.pathname)
        is Cd -> fs.cd(cmd.pathname)
        is Symlink -> fs.symlink(cmd.str, cmd.pathname)
        is ShowStorage -> display.display(storage)
        is Exit -> null
        is Unknown -> unknownCommand()
    }

    private fun unknownCommand(): String {
        return "Unknown command"
    }

    fun output(out: Any?) {
        when (out) {
            is ByteArray -> println(String(out))
            is HardLink -> out(out)
            is StatInfo -> out(out)
            is List<*> -> out(out)
            is Unit -> Unit
            else -> println(out.toString())
        }
    }

    private fun out(out: StatInfo) {
        println("id=${out.id}, type=${out.type}, nlink=${out.nlink}, size=${out.size}, nblock=${out.nblock}\n")
    }

    fun out(links: List<*>) {
        println("--------------------")
        println("${links.size} entries")
        if (links.isEmpty()) {
            return
        }
        println("--------------------")
        when (links[0]) {
            is HardLink -> outLinks(links as List<HardLink>)
            else -> links.forEach { output(it) }
        }
    }

    fun outLinks(links: List<HardLink>) {
        links
            .sortedBy { it.pathname }
            .forEachIndexed { i, link ->
            val name = Path(link.pathname).name()
            val pathname = if (link.file.type == FileType.DIRECTORY) {
                green(name)
            } else blue(name)
            println("${pathname.padEnd(25, ' ')} -> ${link.id}")
        }
    }

    private fun green(s: String) = "\u001B[1;92m$s\u001B[0m"
    private fun blue(s: String) = "\u001B[0;94m$s\u001B[0m"

    fun out(link: HardLink) {
//        println("${link.pathname.padEnd(15, ' ')} -> ${link.id}")
    }

}