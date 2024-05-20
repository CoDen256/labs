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
import kotlin.math.abs

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

    private val directory: Directory = DefaultDirectory()

    private val files = arrayOfNulls<FileDescriptor>(256)
    private val fds = arrayOfNulls<FileStream>(256 * 4)

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
        val fileDescriptorId = directory.get(Path(pathname)).id
        return get(fileDescriptorId).let {
            StatInfo(
                it.id,
                it.type,
                it.size(),
                it.nlink,
                it.nblock()
            )
        }
    }

    override fun ls(): List<HardLink> {
        return directory.ls()
    }

    override fun create(pathname: String): HardLink {
        val fd = newFile()
        return newHardLink(pathname, fd.id)
    }

    private fun newHardLink(path: String, fd: Int): HardLink {
        get(fd).nlink++
        return directory.create(Path(path), fd)
    }

    private fun rmHardLink(path: String) {
        val (name, id) = directory.remove(Path(path))
        get(id).nlink--
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

    private fun newStream(id: Int): FileStream {
        val fd = nextNumericFd()
        val fileStream = FileStream(0, id, fd)
        fds[fd] = fileStream
        return fileStream
    }

    private fun nextNumericFd(): Int{
        for ((index, fileDescriptor) in fds.withIndex()){
            if (fileDescriptor == null){
                return index
            }
        }
        throw IllegalArgumentException("Max amount of file descriptors exceeded: ${fds.size}")
    }


    override fun open(pathname: String): Int {
        return newStream(directory.get(Path(pathname)).id).id
    }

    override fun close(fd: Int) {
        fds[fd] = null
    }

    private fun getFileStream(fd: Int): FileStream {
        return fds[fd] ?: throw IllegalStateException("FD $fd is not open")
    }

    override fun seek(fd: Int, offset: Int) {
        val fileStream = getFileStream(fd)
        fileStream.offset = offset
    }

    private fun FileDescriptor.size(): Int{
        return map.map { storage.getBlock(it) }.count() * storage.getBlockSize()
    }

    private fun FileDescriptor.nblock(): Int{
        return map.map { storage.getBlock(it) }.count { !it.isEmpty() }
    }

    override fun read(fd: Int, size: Int): ByteArray {
        val fileStream = getFileStream(fd)
        val file = get(fileStream.id)

        if (size > file.size()){
            throw IllegalStateException("File size exceeded: ${file.size()}, but was: $size")
        }

        var current = fileStream.offset
        var length = size
        var result = ByteArray(0)

        for (blockNum in file.map) {
            if (length == 0) break
            val block = storage.getBlock(blockNum)

            if (current >storage.getBlockSize()) {
                current -= storage.getBlockSize()
                continue
            }

            val lengthInTheBlock = minOf(length, storage.getBlockSize() - current)

            result += block.read(current, current + lengthInTheBlock)
            length -= lengthInTheBlock
            current = 0
        }
        fileStream.offset += size
        return result
    }

    override fun write(fd: Int, size: Int, value: ByteArray) {
        val fileStream = getFileStream(fd)
        val file = get(fileStream.id)

        if (size > file.size()){
            throw IllegalStateException("File size exceeded: ${file.size()}, but was: $size")
        }

        val blocks = file
            .map
            .map { storage.getBlock(it) }
            .toMutableList()

        var offset = fileStream.offset
        var content = value.sliceArray(0 until size)

        for (block in blocks){
            if (content.isEmpty()) break
            if (offset > storage.getBlockSize()) {
                offset -= storage.getBlockSize()
                continue
            }

            val blockChunkSize = minOf(content.size, storage.getBlockSize() - offset)
            block.write(offset, content.sliceArray(0 until blockChunkSize))
            content = content.drop(blockChunkSize).toByteArray()
            offset = 0
        }

        fileStream.offset += size
    }

    override fun link(pathname1: String, pathname2: String): HardLink {
        return newHardLink(pathname2, directory.get(Path(pathname1)).id)
    }

    override fun unlink(pathname: String) {
        rmHardLink(pathname)
    }

    override fun truncate(name: String, size: Int) {
        val fileDescriptorId = directory.get(Path(name)).id
        val fd = get(fileDescriptorId)
        val diff = size - fd.size()
        if (diff == 0) return
        if (diff > 0) {
            addSize(diff, fd)
            return
        }
        subSize(diff, fd)
    }

    override fun mkdir(pathname: String): HardLink {
        return directory.create(Path(pathname), newDir().id)
    }

    override fun rmdir(pathname: String): HardLink {
        return directory.remove(Path(pathname))
    }

    override fun cd(pathname: String): HardLink {
        return directory.cd(Path(pathname))
    }

    override fun cwd(): String {
        return directory.path().toString()
    }

    override fun symlink(name: String) {
        TODO("Not yet implemented")
    }

    private fun subSize(size: Int, fd: FileDescriptor) {
        val fullBlocksCount = abs(size / storage.getBlockSize())

        repeat(fullBlocksCount) {
            val index = fd.map.size - it - 1
            val blockId = fd.map.removeAt(index)
            storage.removeBlock(blockId)
        }
    }

    private fun addSize(size: Int, fd: FileDescriptor) {
        val fullBlocksCount = (size / storage.getBlockSize())
        val leftBlockSize = size % storage.getBlockSize()
        repeat(fullBlocksCount) {
            val block = storage.newBlock()
            fd.map.add(block.getId())
        }

        if (leftBlockSize != 0) {
            fd.map.add(storage.newBlock().getId())
        }
    }


    internal fun get(fileDescriptorId: Int): FileDescriptor {
        return files[fileDescriptorId] ?: throw FileNotFoundException("FD $fileDescriptorId Not Found")
    }
}