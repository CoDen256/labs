package io.github.aljolen

import io.github.aljolen.components.INodeCounter
import io.github.aljolen.components.factories.DirFactory
import io.github.aljolen.components.factories.HardLinkFactory
import io.github.aljolen.components.factories.SoftLinkFactory
import io.github.aljolen.components.operations.FileEdit
import io.github.aljolen.components.operations.FileIO
import io.github.aljolen.components.operations.Navigation
import io.github.aljolen.components.operations.Printer
import io.github.aljolen.fs.*
import io.github.aljolen.fs.storage.Storage
import io.github.aljolen.utils.Link
import java.io.FileNotFoundException
import kotlin.collections.List

class DefaultFileSystem(
    private val storage: Storage
) : FS {
    private var iNodeCounter: INodeCounter = INodeCounter()
    private var hardLinkFactory: HardLinkFactory = HardLinkFactory(iNodeCounter)
    private var dirFactory: DirFactory = DirFactory(iNodeCounter)
    private var softLinkFactory: SoftLinkFactory = SoftLinkFactory(iNodeCounter)
    private var printer: Printer = Printer()
    private var fileIO: FileIO
    private var navigation: Navigation
    private var fileEdit: FileEdit

    private val directory: WorkingDirectory = WorkingDirectoryTree()
    private val io: IO = DefaultIO(storage)

    private val files = arrayOfNulls<FileDescriptor>(256)

    init {
        val rootDir = dirFactory.create("root")
        navigation = Navigation(rootDir, rootDir)
        fileIO = FileIO(1000)
        fileEdit = FileEdit(iNodeCounter, fileIO)
    }

    fun create(link: Link) {
        val (path, name) = link.sliceLast()
        val dir = navigation.getDir(path)
        dir.add(hardLinkFactory.create(name))
    }

    fun open(name: Link): Int {
        val (_, file) = navigation.getLinkWithFile(name)
        return fileEdit.open(file)
    }

//    fun close(iNode: Int) {
//        fileEdit.close(iNode)
//    }

//    fun seek(iNode: Int, offset: Int) {
//        fileEdit.seek(iNode, offset)
//    }
//
//    fun read(iNode: Int, size: Int): String {
//        return fileEdit.read(iNode, size)
//    }

//    fun write(iNode: Int, size: Int, value: String) {
//        fileEdit.write(iNode, size, value)
//    }

    fun truncate(name: Link, size: Int) {
        val (_, file) = navigation.getLinkWithFile(name)
        fileIO.truncate(file, size)
    }

    fun stat(link: Link): String {
        val (searchedLink, file) = navigation.getLinkWithFile(link)
        return printer.stat(searchedLink, file, 1024, link)
    }

    fun link(link1: Link, link2: Link) {
        val (path, name) = link2.sliceLast()
        val dir = navigation.getDir(path)
        val refHardLink = navigation.getHardLink(link1)
        dir.add(hardLinkFactory.duplicate(name, refHardLink))
    }

    fun symlink(name1: Link, name2: Link) {
        val (path, name) = name2.sliceLast()
        val dir = navigation.getDir(path)
        val value = navigation.getHardLinkOrDir(name1)
        dir.add(softLinkFactory.create(name, value))
    }

    fun mkdir(link: Link) {
        val (path, name) = link.sliceLast()
        val dir = navigation.getDir(path)
        dir.add(dirFactory.create(name))
    }

    fun unlink(link: Link) {
        val searchedLink = navigation.getLink(link)
        searchedLink.remove()
    }

    fun rmdir(link: Link) {
        val dir = navigation.getDir(link)
        dir.remove()
    }

    fun cd(link: Link) {
        navigation.setDir(link)
    }

    fun ls(link: Link): String {
        val dir = navigation.getDir(link)
        return printer.getDirChildrenInfo(dir)
    }

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