package io.github.aljolen.fs

import io.github.aljolen.fs.api.*
import java.io.FileNotFoundException
import java.nio.file.FileAlreadyExistsException

class WorkingDirectoryTree(io: IO, rootFile: FileDescriptor) : WorkingDirectory {

    private val root: Directory = FSDirectory(io, HardLink("root", rootFile))
    private var cwd: Directory = root

    override fun symlink(path: Path, file: FileDescriptor): HardLink {
        val parent = getDir(path.parent())
        return parent.symlink(path.name(), file)
    }

    override fun link(path: Path, file: FileDescriptor): HardLink {
        val parent = getDir(path.parent())
        return parent.link(path.name(), file)
    }

    override fun mkdir(path: Path, file: FileDescriptor): HardLink {
        val parent = getDir(path.parent())
        return parent.mkdir(path.name(), file)
    }

    override fun ls(path: Path): List<HardLink> {
        return getDir(path).ls()
    }

    override fun get(path: Path): HardLink {
        return getNode(path).value()
    }

    override fun getSymlink(path: Path): HardLink {
        val parent = getDir(path.parent())
        return parent.get(path.name()).value()
    }

    override fun cwd(): Path {
        return cwd.path()
    }

    override fun cd(path: Path): HardLink {
        cwd = getDir(path)
        return cwd.value()
    }

    override fun unlink(path: Path): HardLink {
        val parent = getDir(path.parent())
        return parent.unlink(path.name())
    }

    override fun rmdir(path: Path): HardLink {
        val dir = getDir(path)
        dir.delete()
        return dir.value()
    }

    private fun getDir(path: Path?): Directory {
        if (path == null) {return cwd}
        val node = getNode(path)
        return node as? Directory ?: throw IllegalArgumentException("$path is not a directory")
    }

    private fun getNode(path: Path): DirectoryEntry {
        return getNode(cwd, path)
    }

    private fun traverse(root: DirectoryEntry, segments: List<String>): DirectoryEntry {
        if (segments.isEmpty()) return root
        if (root !is Directory){
            throw IllegalArgumentException("$root is not a directory")
        }

        val next = segments.first()
        val node = root.get(next)
        if (node is Symlink){
            return getNode(root, node.resolve())
        }
        return traverse(node, segments.drop(1))
    }

    private fun getNode(relative: Directory, path: Path): DirectoryEntry {
        if (path.isRelative()) {
            return traverse(relative, path.segments())
        }
        return traverse(root, path.segments())
    }

}

class FSSymlink(private val value: HardLink, private val io: IO): Symlink {

    override fun resolve(): Path {
        return Path(io.readSymlink(value.file))
    }

    override fun value(): HardLink {
        return value
    }

    override fun path(): Path {
        return Path(value.pathname)
    }
}

class FSFile(private val value: HardLink) : File {
    override fun value(): HardLink {
        return value
    }

    override fun path(): Path {
        return Path(value.pathname)
    }
}

class FSDirectory(
    private val io: IO,
    private val link: HardLink,
    private val parent: Directory?=null,
) : Directory {

    private val nodes = HashMap<HardLink, DirectoryEntry>()

    init {
        newNode(newHardLink(".", link.file), this)
        if (parent != null){
            newNode(newHardLink("..", parent.value().file), parent)
        }
    }

    override fun value(): HardLink = link

    override fun symlink(name: String, file: FileDescriptor): HardLink {
        verifyDuplicate(name)
        val link = newHardLink(name, file)
        newNode(link, FSSymlink(link, io))
        return link
    }

    override fun link(name: String, file: FileDescriptor): HardLink {
        verifyDuplicate(name)
        val link = newHardLink(name, file)
        newNode(link, FSFile(link))
        return link
    }

    private fun newNode(link: HardLink, node: DirectoryEntry) {
        nodes[link] = node
    }

    override fun mkdir(name: String, file: FileDescriptor): HardLink {
        verifyDuplicate(name)
        val link = newHardLink(name, file)
        newNode(link, FSDirectory(io, link, this))
        return link
    }

    override fun unlink(name: String): HardLink {
        val link = getLink(name)
        if (link.file.type == FileType.DIRECTORY) throw IllegalStateException("Not allowed to unlink directories ($name)")
        nodes.remove(link)
        link.file.nlink--
        return link
    }

    override fun forceUnlink(name: String): HardLink {
        val link = getLink(name)
        nodes.remove(link)
        link.file.nlink--
        return link
    }

    private fun getLink(name: String): HardLink {
        return nodes.keys.find { Path(it.pathname).name() == name } ?: throw FileNotFoundException("File with name <$name> does not exist")
    }

    override fun get(name: String): DirectoryEntry {
        return nodes[getLink(name)] ?: throw FileNotFoundException("Hardlink $name not found")
    }

    override fun ls(): List<HardLink> {
        return nodes.map { it.key }
    }

    override fun delete() {
        if (nodes.size > 2) throw IllegalStateException("Unable to remove non-empty directory ${value().pathname}")
        forceUnlink(".")
        if (parent != null){
            forceUnlink("..")
            parent.forceUnlink(path().name())
        }
    }

    override fun path(): Path {
        return Path(link.pathname)
    }

    private fun newHardLink(name: String, file: FileDescriptor): HardLink {
        return HardLink(path().resolve(name).toString(), file).also {
            file.nlink++
        }
    }

    private fun verifyDuplicate(name: String) {
        if (nodes.keys.any { Path(it.pathname).name() == name }) {
            throw FileAlreadyExistsException("File with name $name already exists")
        }
    }
}
