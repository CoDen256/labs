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
    private val fds = ArrayList<FileDescriptor>()

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

    fun close(iNode: Int) {
        fileEdit.close(iNode)
    }

    fun seek(iNode: Int, offset: Int) {
        fileEdit.seek(iNode, offset)
    }

    fun read(iNode: Int, size: Int): String {
        return fileEdit.read(iNode, size)
    }

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
        TODO("Not yet implemented")
    }

    override fun ls(): List<HardLink> {
        return links
    }


    override fun create(name: String): HardLink {
        if (links.any{it.name == name}){ throw FileAlreadyExistsException("File with name $name already exists") }
        val fd = newFile()
        fds.add(fd)

        return newHardLink(name, fd.id)
    }

    private fun newHardLink(name: String, fd: FileDescriptorId): HardLink {
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
        return FileDescriptor(nextFileDescriptorId(), FileType.REGULAR, 0, 0, java.util.ArrayList())
    }
    private fun nextFileDescriptorId() = FileDescriptorId(fds.size)

    override fun open(fd: FileDescriptorId) {
        TODO("Not yet implemented")
    }

    override fun open(name: String): FileDescriptorId {
        TODO("Not yet implemented")
    }

    override fun close(fd: FileDescriptorId) {
        TODO("Not yet implemented")
    }

    override fun seek(fd: FileDescriptorId, offset: Long) {
        TODO("Not yet implemented")
    }

    override fun read(fd: FileDescriptorId, size: Long) {
        TODO("Not yet implemented")
    }

    override fun write(fd: FileDescriptorId, size: Long) {
        TODO("Not yet implemented")
    }

    override fun link(name1: String, name2: String): HardLink {
        return newHardLink(name2, get(name1))
    }

    override fun unlink(name: String) {
        rmHardLink(name)
    }

    override fun truncate(name: String, size: Long) {
        TODO("Not yet implemented")
    }


    fun get(fileDescriptorId: FileDescriptorId): FileDescriptor {
        return fds.find { it.id == fileDescriptorId } ?: throw FileNotFoundException("FD $fileDescriptorId Not Found")
    }

    fun get(name: String): FileDescriptorId {
        return links.find { it.name == name }?.id ?: throw FileNotFoundException("File <$name> Not Found")
    }

}