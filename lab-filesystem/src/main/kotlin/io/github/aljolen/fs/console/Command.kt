package io.github.aljolen.fs.console

sealed interface Command
data object Exit : Command
data object Unknown : Command
data object ShowStorage: Command

data class Stat(val pathname: String) : Command
data class Ls(val pathname: String) : Command
data class Create(val pathname: String) : Command
data class Open(val pathname: String) : Command
data class Close(val fd: Int) : Command
data class Seek(val fd: Int, val offset: Int) : Command
data class Read(val fd: Int, val size: Int) : Command
data class Write(val fd: Int, val size: Int, val content: ByteArray) : Command
data class Link(val pathname1: String, val pathname2: String) : Command
data class Unlink(val pathname: String) : Command
data class Truncate(val pathname: String, val size: Int) : Command
data class Mkdir(val pathname: String) : Command
data class Rmdir(val pathname: String) : Command
data class Cd(val pathname: String) : Command
data class Symlink(val str: String, val pathname: String) : Command