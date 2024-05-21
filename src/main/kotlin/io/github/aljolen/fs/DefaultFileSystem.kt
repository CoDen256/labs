package io.github.aljolen.fs

import io.github.aljolen.fs.api.*
import java.io.FileNotFoundException

class DefaultFileSystem(storage: Storage) : FS {

    private val directory: WorkingDirectory = WorkingDirectoryTree()
    private val io: IO = DefaultIO(storage)

    private val files = arrayOfNulls<FileDescriptor>(256)

    override fun stat(pathname: String): StatInfo {
        return io.stat(get(directory.get(Path(pathname)).id))
    }

    override fun ls(): List<HardLink> {
        return directory.ls()
    }

    override fun create(pathname: String): HardLink {
        val fd = newFile()
        return newFile(pathname, fd.id)
    }

    override fun open(pathname: String): Int {
        return io.open(get(directory.get(Path(pathname)).id))
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
        return newFile(pathname2, directory.get(Path(pathname1)).id)
    }

    override fun unlink(pathname: String) {
        rmFile(pathname)
    }

    override fun truncate(pathname: String, size: Int) {
        return io.truncate(get(directory.get(Path(pathname)).id), size)
    }

    override fun mkdir(pathname: String): HardLink {
        return newDir(pathname, newDir().id)
    }

    override fun rmdir(pathname: String): HardLink {
        return rmDir(pathname)
    }

    override fun cd(pathname: String): HardLink {
        return directory.cd(Path(pathname))
    }

    override fun cwd(): String {
        return directory.cwd().toString()
    }

    override fun symlink(value: String, pathname: String) {
        directory.symlink(Path(value), Path(pathname))
    }

    internal fun get(fileDescriptorId: Int): FileDescriptor {
        return files[fileDescriptorId] ?: throw FileNotFoundException("FD $fileDescriptorId Not Found")
    }

    private fun newFile(path: String, fd: Int): HardLink {
        val file = get(fd)
        return directory.create(Path(path), file)
    }

    private fun rmFile(path: String) {
        get(directory.remove(Path(path)).id)
    }

    private fun newDir(path: String, fd: Int): HardLink {
        val file = get(fd)
        return directory.mkdir(Path(path), file)
    }

    private fun rmDir(path: String): HardLink {
        val dir = directory.rmdir(Path(path))
        return dir
    }

    private fun newFile(): FileDescriptor {
        val fd = nextFileDescriptorId()
        val new = FileDescriptor(fd, FileType.REGULAR, 0)
        files[fd] = new
        return new
    }

    private fun newDir(): FileDescriptor {
        val fd = nextFileDescriptorId()
        val new = FileDescriptor(fd, FileType.DIRECTORY, 0)
        files[fd] = new
        return new
    }

    private fun nextFileDescriptorId(): Int {
        for ((index, fileDescriptor) in files.withIndex()) {
            if (fileDescriptor == null){
                return index
            }
        }
        throw IllegalArgumentException("Max amount of file descriptors exceeded: ${files.size}")
    }
}