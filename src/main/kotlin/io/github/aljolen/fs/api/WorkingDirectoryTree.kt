package io.github.aljolen.fs.api


sealed interface DirectoryEntry {
    fun value(): HardLink
    fun path(): Path
}

interface File : DirectoryEntry

interface Directory : DirectoryEntry {
    fun symlink(name: String, file: FileDescriptor): HardLink
    fun link(name: String, file: FileDescriptor): HardLink
    fun mkdir(name: String, file: FileDescriptor): HardLink
    fun unlink(name: String): HardLink
    fun get(name: String): DirectoryEntry
    fun ls(): List<HardLink>
    fun delete()
}

interface Symlink: DirectoryEntry{
    fun resolve(): Path
}

interface WorkingDirectory {
    fun symlink(path: Path, file: FileDescriptor): HardLink
    fun link(path: Path, file: FileDescriptor): HardLink
    fun mkdir(path: Path, file: FileDescriptor): HardLink
    fun unlink(path: Path): HardLink
    fun rmdir(path: Path): HardLink
    fun ls(): List<HardLink>
    fun get(path: Path): HardLink
    fun getSymlink(path: Path): HardLink
    fun cwd(): Path
    fun cd(path: Path): HardLink
}
