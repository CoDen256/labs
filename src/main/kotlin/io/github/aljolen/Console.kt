package io.github.aljolen

import io.github.aljolen.fs.FS
import io.github.aljolen.fs.HardLink
import io.github.aljolen.fs.StatInfo
import io.github.aljolen.fs.storage.Storage
import io.github.aljolen.utils.StorageDisplay

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
            }catch (e:Exception){
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
            "write" -> Write(args[1].toInt(), args[2].toInt(), args.subList(3, args.size).joinToString(" ").toByteArray())
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
        when(out){
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

    fun out(links: List<*>){
        println("--------------------")
        println("${links.size} entries")
        if (links.isEmpty()){ return }
        println("--------------------")
        when(links[0]){
            is HardLink -> outLinks(links as List<HardLink>)
            else -> links.forEach { output(it)}
        }
    }

    fun outLinks(links: List<HardLink>){
        links.forEachIndexed { i, link ->
            println("${link.pathname.padEnd(15, ' ')} -> ${link.id}")
        }
    }

    fun out(link: HardLink){
        println("${link.pathname.padEnd(15, ' ')} -> ${link.id}")
    }

}


sealed interface Command {
}

data object Exit : Command
data object Unknown : Command
data object ShowStorage: Command

data class Stat(val pathname: String, ) : Command
data object Ls : Command
data class Create(val pathname: String, ) : Command
data class Open(val pathname: String, ) : Command
data class Close(val fd: Int, ) : Command
data class Seek(val fd: Int, val offset: Int, ) : Command
data class Read(val fd: Int, val size: Int, ) : Command
data class Write(val fd: Int, val size: Int, val content: ByteArray, ) : Command
data class Link(val pathname1: String, val pathname2: String, ) : Command
data class Unlink(val pathname: String, ) : Command
data class Truncate(val pathname: String, val size: Int, ) : Command
data class Mkdir(val pathname: String, ) : Command
data class Rmdir(val pathname: String, ) : Command
data class Cd(val pathname: String, ) : Command
data class Symlink(val str: String, val pathname: String, ) : Command
