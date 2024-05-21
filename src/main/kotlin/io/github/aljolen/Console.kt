package io.github.aljolen

import io.github.aljolen.fs.FS
import io.github.aljolen.fs.HardLink

class Console(private val fs: FS) {

    fun run() {
        while (true) {
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
            "list" -> List
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
            else -> Unknown
        }
    }

    fun handle(cmd: Command): Any? = when (cmd) {
        is Stat -> fs.stat(cmd.pathname)
        is List -> fs.ls()
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
        is Exit -> null
        is Unknown -> unknownCommand()
    }

    private fun unknownCommand() {
        println("Unknown command")
    }


    fun output(out: Any) {
        when(out){
            is HardLink -> output(out)
            else -> println(out.toString())
        }
    }

    fun out(link: HardLink){

    }

    fun out(link: HardLink){
        println("$link")
    }

}


sealed interface Command {
    val args: Int get() = 0
    fun cmd(): String = this.javaClass.simpleName.lowercase()
}

data object Exit : Command
data object Unknown : Command
data object Storage: Command

data class Stat(val pathname: String, override val args: Int = 1) : Command
data object List : Command
data class Create(val pathname: String, override val args: Int = 1) : Command
data class Open(val pathname: String, override val args: Int = 1) : Command
data class Close(val fd: Int, override val args: Int = 1) : Command
data class Seek(val fd: Int, val offset: Int, override val args: Int = 2) : Command
data class Read(val fd: Int, val size: Int, override val args: Int = 2) : Command
data class Write(val fd: Int, val size: Int, val content: ByteArray, override val args: Int = 3) : Command
data class Link(val pathname1: String, val pathname2: String, override val args: Int = 2) : Command
data class Unlink(val pathname: String, override val args: Int = 1) : Command
data class Truncate(val pathname: String, val size: Int, override val args: Int = 2) : Command
data class Mkdir(val pathname: String, override val args: Int = 1) : Command
data class Rmdir(val pathname: String, override val args: Int = 1) : Command
data class Cd(val pathname: String, override val args: Int = 1) : Command
data class Symlink(val str: String, val pathname: String, override val args: Int = 2) : Command
