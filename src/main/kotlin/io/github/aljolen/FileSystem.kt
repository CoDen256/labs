package io.github.aljolen

import io.github.aljolen.components.INodeCounter
import io.github.aljolen.components.factories.DirFactory
import io.github.aljolen.components.factories.HardLinkFactory
import io.github.aljolen.components.factories.SoftLinkFactory
import io.github.aljolen.components.operations.FileEdit
import io.github.aljolen.components.operations.FileIO
import io.github.aljolen.components.operations.Navigation
import io.github.aljolen.components.operations.Printer
import io.github.aljolen.utils.Link

class FileSystem {
    private lateinit var iNodeCounter: INodeCounter
    private lateinit var hardLinkFactory: HardLinkFactory
    private lateinit var dirFactory: DirFactory
    private lateinit var softLinkFactory: SoftLinkFactory
    private lateinit var printer: Printer
    private lateinit var fileIO: FileIO
    private lateinit var navigation: Navigation
    private lateinit var fileEdit: FileEdit
    private var ioSizeInBytes: Int = 0

    constructor(n: Int, ioSizeInBytes: Int) {
        val rootDir = dirFactory.create("root")
        navigation = Navigation(rootDir, rootDir)
        this.ioSizeInBytes = ioSizeInBytes
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


}