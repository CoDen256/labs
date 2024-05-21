package io.github.aljolen.fs.api

import java.io.FileNotFoundException
import java.nio.file.FileAlreadyExistsException


interface WorkingDirectory {
    fun create(path: Path, file: FileDescriptor): HardLink
    fun mkdir(path: Path, file: FileDescriptor): HardLink
    fun remove(path: Path): HardLink
    fun rmdir(path: Path): HardLink
    fun ls(): List<HardLink>
    fun get(path: Path): HardLink
    fun cwd(): Path
    fun cd(path: Path): HardLink
    fun symlink(value: Path, path: Path)
}

sealed interface Node {
    fun value(): HardLink
    fun path(): Path
    fun fd(): Int
    fun nodes(): List<Node>
    fun name(): String
}

interface File : Node {}

interface Directory : Node {
    fun create(name: String, file: FileDescriptor): HardLink
    fun mkdir(name: String, file: FileDescriptor): HardLink
    fun remove(name: String): HardLink
    fun get(name: String): Node
    fun delete()
}

class FSFile(private val value: HardLink) : File {
    override fun value(): HardLink {
        return value
    }

    override fun path(): Path {
        return Path(value.pathname)
    }

    override fun fd(): Int {
        return value.id
    }

    override fun nodes(): List<Node> {
        return emptyList()
    }

    override fun name(): String {
        return path().name()
    }

}

fun HardLink.asNode(parent: Directory?): Node {
    return when (this.file.type) {
        FileType.REGULAR -> FSFile(this)
        FileType.DIRECTORY -> FSDirectory(this, parent)
    }
}


class FSDirectory(
    private val value: HardLink,
    private val parent: Directory?=null
) : Directory {

    private val links = ArrayList<HardLink>()

    init {
        mkdir(".", value.file)
        parent?.let { mkdir("..", it.value().file) }
    }

    override fun value(): HardLink = value

    override fun create(name: String, file: FileDescriptor): HardLink {
        verifyDuplicate(name)
        val link = newHardLink(name, file)
        links.add(link)
        file.nlink++
        return link
    }

    override fun mkdir(name: String, file: FileDescriptor): HardLink {
        verifyDuplicate(name)
        val link = newHardLink(name, file)
        links.add(link)
        file.nlink++
        return link
    }

    override fun remove(name: String): HardLink {
        val link = getLink(name)
        if (link.file.type == FileType.DIRECTORY) {throw IllegalArgumentException("$name is a directory")}
        links.remove(link)
        link.file.nlink--
        return link
    }

    private fun getLink(name: String): HardLink {
        return links.find { Path(it.pathname).name() == name } ?: throw FileNotFoundException("File with name $name does not exist")
    }

    override fun get(name: String): Node {
        if (name == ".") return this
        if (name == "..") return parent ?: throw FileNotFoundException("Directory with name $name does not exist")
        return getLink(name).asNode(this)
    }

    override fun delete() {
        value.file.nlink--
//        for (node in links) {
//            if (node is Directory) {node.delete()}
//            else remove(node.name())
//        }
    }

    override fun path(): Path {
        return Path(value.pathname)
    }

    override fun fd(): Int {
        return value.id
    }

    override fun nodes(): List<Node> {
        return links.map { it.asNode(this) }
    }

    override fun name(): String {
        return path().name()
    }

    private fun newHardLink(name: String, file: FileDescriptor) = HardLink(path().resolve(name).toString(), file)

    private fun verifyDuplicate(name: String) {
        if (links.any { Path(it.pathname).name() == name }) {
            throw FileAlreadyExistsException("File with name $name already exists")
        }
    }
}

class WorkingDirectoryTree : WorkingDirectory {

    private val root: Directory = FSDirectory(HardLink("root", FileDescriptor.ROOT))
    private var cwd: Directory = root

    override fun create(path: Path, file: FileDescriptor): HardLink {
        val parent = getDir(path.parent())
        return parent.create(path.name(), file)
    }

    override fun mkdir(path: Path, file: FileDescriptor): HardLink {
        val parent = getDir(path.parent())
        return parent.mkdir(path.name(), file)
    }

    override fun ls(): List<HardLink> {
        return cwd.nodes().map { it.value() }
    }

    override fun get(path: Path): HardLink {
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

    override fun symlink(value: Path, path: Path) {
        TODO("Not yet implemented")
    }

    override fun remove(path: Path): HardLink {
        val parent = getDir(path.parent())
        return parent.remove(path.name())
    }

    override fun rmdir(path: Path): HardLink {
        val dir = getDir(path)
        dir.delete()
        return dir.value()
    }

    private fun getDir(path: Path?): Directory {
        if (path == null) {return cwd}
        return getNode(path) as? Directory ?: throw IllegalArgumentException("$path is not a directory")
    }

    private fun getNode(path: Path): Node {
        if (path.isRelative()) {
            return traverse(cwd, path.segments())
        }
        return traverse(root, path.segments())
    }

    private fun traverse(root: Directory, segments: List<String>): Node {
        if (segments.isEmpty()) return root
        val next = segments.first()
        val node = root.get(next)
        if (node !is Directory) {
            throw IllegalArgumentException("$next is not a directory")
        }
        return traverse(node, segments.drop(1))
    }

}


class Path(private val path: String) {

    fun resolve(subPath: String): Path {
        return Path(this.path + "/" + subPath)
    }

    fun isRelative(): Boolean {
        return !path.startsWith("/")
    }

    fun name(): String = elements.last()
    fun parent(): Path? {
        if (elements.size == 1){ return null}
        return Path(elements.dropLast(1).joinToString("/"))
    }

    private val elements: Array<String>;

    init {
        val clean = path.ifBlank { "." }

        elements = clean
            .replace(Regex("\\/\$"), "")
            .split(Regex("(?<!\\\\)\\/"))
            .toTypedArray()
    }

    fun segments(): List<String> {
        return elements.map { it.replace(Regex("/\\\\(?=\\/)/g"), "") }
    }

    override fun toString(): String {
        return elements.joinToString("/").removePrefix("root")
    }

}