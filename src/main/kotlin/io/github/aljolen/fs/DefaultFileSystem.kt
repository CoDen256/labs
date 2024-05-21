package io.github.aljolen.fs

import io.github.aljolen.fs.api.*
import java.io.FileNotFoundException

class DefaultFileSystem(
    private val io: IO,
) : FS {

    private val files = arrayOfNulls<FileDescriptor>(256)
    private val root: FileDescriptor = newDir()
    private val directory: WorkingDirectory = WorkingDirectoryTree(io, root)

    override fun stat(pathname: String): StatInfo {
        return io.stat(directory.getSymlink(Path(pathname)).file)
    }

    override fun ls(pathname: String): List<HardLink> {
        return directory.ls(Path(pathname))
    }

    override fun create(pathname: String): HardLink {
        val file = nextFile()
        return linkfile(pathname, file).also { save(file) }
    }

    override fun open(pathname: String): Int {
        return io.open(directory.get(Path(pathname)).file)
    }

    override fun close(fd: Int) {
        return io.close(fd)
    }

    override fun seek(fd: Int, offset: Int) {
        io.seek(fd, offset)
    }

    override fun read(fd: Int, size: Int): ByteArray {
        return io.read(fd, size)
    }

    override fun write(fd: Int, size: Int, value: ByteArray) {
        io.write(fd, size, value)
    }

    override fun link(pathname1: String, pathname2: String): HardLink {
        val file = directory.getSymlink(Path(pathname1)).file
        return linkfile(pathname2, file)
    }

    override fun unlink(pathname: String) {
        directory.unlink(Path(pathname))
    }

    override fun truncate(pathname: String, size: Int) {
        return io.truncate(directory.get(Path(pathname)).file, size)
    }

    override fun mkdir(pathname: String): HardLink {
        val new = nextDir()
        return linkdir(pathname, new).also {
            save(new)
        }
    }

    override fun rmdir(pathname: String): HardLink {
        return directory.rmdir(Path(pathname))
    }

    override fun cd(pathname: String): HardLink {
        return directory.cd(Path(pathname))
    }

    override fun cwd(): String {
        return directory.cwd().toString()
    }

    override fun symlink(value: String, pathname: String): HardLink {
        val file = nextSymLink()
        val link = directory.symlink(Path(pathname), file)
        io.writeSymlink(value, file)
        save(file)
        return link
    }

    private fun linkfile(path: String, file: FileDescriptor): HardLink {
        return directory.link(Path(path), file)
    }

    private fun linkdir(path: String, file: FileDescriptor): HardLink {
        return directory.mkdir(Path(path), file)
    }

    private fun newDir(): FileDescriptor {
        val new = nextDir()
        save(new)
        return new
    }

    private fun nextDir(): FileDescriptor {
        val fd = nextFileDescriptorId()
        return FileDescriptor(fd, FileType.DIRECTORY, 0)
    }

    private fun nextFile(): FileDescriptor {
        val fd = nextFileDescriptorId()
        return FileDescriptor(fd, FileType.REGULAR, 0)
    }

    private fun nextSymLink(): FileDescriptor{
        val fd = nextFileDescriptorId()
        return FileDescriptor(fd, FileType.SYMBOLIC, 0)
    }

    private fun save(new: FileDescriptor) {
        files[new.id] = new
    }

    internal fun get(fileDescriptorId: Int): FileDescriptor {
        return files[fileDescriptorId] ?: throw FileNotFoundException("FD $fileDescriptorId Not Found")
    }

    private fun nextFileDescriptorId(): Int {
        for ((index, fileDescriptor) in files.withIndex()) {
            if (fileDescriptor == null) {
                return index
            }
        }
        throw IllegalArgumentException("Max amount of file descriptors exceeded: ${files.size}")
    }
}