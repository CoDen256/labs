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
import io.github.aljolen.fs.storage.MemoryStorage
import io.github.aljolen.utils.Link
import java.io.FileNotFoundException
import java.nio.file.FileAlreadyExistsException

class DefaultFileSystem(
    private var ioSizeInBytes: Int
) : FS {
    private var iNodeCounter: INodeCounter = INodeCounter()
    private var hardLinkFactory: HardLinkFactory = HardLinkFactory(iNodeCounter)
    private var dirFactory: DirFactory = DirFactory(iNodeCounter)
    private var softLinkFactory: SoftLinkFactory = SoftLinkFactory(iNodeCounter)
    private var printer: Printer = Printer()
    private var fileIO: FileIO
    private var navigation: Navigation
    private var fileEdit: FileEdit

    private val links = ArrayList<HardLink>()
    private val files = arrayOfNulls<FileDescriptor>(256)
    private val fds = arrayOfNulls<FileStream>(256 * 4)
    private val storage = MemoryStorage(256, 256)

    init {
        val rootDir = dirFactory.create("root")
        navigation = Navigation(rootDir, rootDir)
        fileIO = FileIO(ioSizeInBytes)
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

    fun write(iNode: Int, size: Int, value: String) {
        fileEdit.write(iNode, size, value)
    }

    fun truncate(name: Link, size: Int) {
        val (_, file) = navigation.getLinkWithFile(name)
        fileIO.truncate(file, size)
    }

    fun stat(link: Link): String {
        val (searchedLink, file) = navigation.getLinkWithFile(link)
        return printer.stat(searchedLink, file, ioSizeInBytes, link)
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

    override fun stat(name: String): StatInfo {
        return get(get(name)).let {
            StatInfo(
                it.id,
                it.type,
                it.size,
                it.nlink,
                it.nblock
            )
        }
    }

    override fun ls(): List<HardLink> {
        return links
    }

    override fun create(name: String): HardLink {
        val fd = newFile()

        return newHardLink(name, fd.id)
    }

    private fun newHardLink(name: String, fd: Int): HardLink {
        if (links.any{it.name == name}){ throw FileAlreadyExistsException("File with name $name already exists") }
        val hardLink = HardLink(name, fd)
        links.add(hardLink)
        get(fd).nlink++
        return hardLink
    }

    private fun rmHardLink(name: String) {
        val link: HardLink = links.firstOrNull { it.name == name } ?: throw FileNotFoundException("No link with name $name")
        links.remove(link)
        get(link.id).nlink--
    }

    private fun newFile(): FileDescriptor {
        val fd = nextFileDescriptorId()
        val new = FileDescriptor(fd, FileType.REGULAR, 0,  java.util.ArrayList())
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


    override fun open(name: String): Int {
        return newStream(get(name)).id
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

    override fun read(fd: Int, size: Int): ByteArray {
        val fileStream = getFileStream(fd)
        val file = get(fileStream.id)

        var current = fileStream.offset
        var length = size
        var result = ByteArray(size)

        for (blockNum in file.map) {
            if (length == 0) break
            val block = storage.getBlock(blockNum)

            if (current >storage.getBlockSize()) {
                current -= storage.getBlockSize()
                println(current)
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

        var left = fileStream.offset
        val blocks = file.map.map { storage.getBlock(it) }.toMutableList()
        var block = blocks.removeFirstOrNull()

        var fullValue = value

        while (block != null && fullValue.isNotEmpty()) {
            if (left > storage.getBlockSize()) {
                left -= storage.getBlockSize()
                continue
            }

            val blockSize = storage.getBlockSize()
            val writeLength = blockSize - left
            val toWrite = fullValue.slice(0 until writeLength)

            fullValue = fullValue.drop(writeLength).toByteArray()
            block.write(left, toWrite.toByteArray())

            left = 0
            block = blocks.removeFirstOrNull()
        }
        fileStream.offset += size
    }

    override fun link(name1: String, name2: String): HardLink {
        return newHardLink(name2, get(name1))
    }

    override fun unlink(name: String) {
        rmHardLink(name)
    }

    override fun truncate(name: String, size: Int) {
        val fullBlocksCount = (size / storage.getBlockSize())
        val leftBlockSize = size % storage.getBlockSize()
        val fd = get(get(name))
        repeat(fullBlocksCount) {
            val block = storage.newBlock()
            fd.map.add(block.getId())
        }

//        if (leftBlockSize != 0) file.addBlock(Block(leftBlockSize))
        fd.map.add(storage.newBlock().getId())
    }


    internal fun get(fileDescriptorId: Int): FileDescriptor {
        return files[fileDescriptorId] ?: throw FileNotFoundException("FD $fileDescriptorId Not Found")
    }

    internal fun get(name: String): Int {
        return links.find { it.name == name }?.id ?: throw FileNotFoundException("File <$name> Not Found")
    }
}