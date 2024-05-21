package io.github.aljolen.fs

import io.github.aljolen.utils.Link
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
    fun create(name: String, file: FileDescriptor): File
    fun mkdir(name: String, file: FileDescriptor): Directory
    fun remove(name: String): Node
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

class FSDirectory(private val value: HardLink) : Directory {

    private val nodes = ArrayList<Node>().also {
        // TODO infinite recursion
    }

    override fun value(): HardLink = value

    override fun create(name: String, file: FileDescriptor): File {
        verifyDuplicate(name)
        val file = FSFile(newHardLink(name, file))
        nodes.add(file)
        return file
    }

    override fun mkdir(name: String, file: FileDescriptor): Directory {
        verifyDuplicate(name)
        val dir = FSDirectory(newHardLink(name, file))
        nodes.add(dir)
        return dir
    }

    override fun remove(name: String): Node {
        val node = get(name)
        if (node is Directory) {throw IllegalArgumentException("$name is a directory")}
        nodes.remove(node)
        return node
    }

    override fun get(name: String): Node {
        if (name == ".") return this
        return nodes.find { it.name() == name } ?: throw FileNotFoundException("File with name $name does not exist")
    }

    override fun delete() {
        for (node in nodes) {
            if (node is Directory) {node.delete()}
            else remove(node.name())
        }
    }

    override fun path(): Path {
        return Path(value.pathname)
    }

    override fun fd(): Int {
        return value.id
    }

    override fun nodes(): List<Node> {
        return nodes
    }

    override fun name(): String {
        return path().name()
    }

    private fun newHardLink(name: String, file: FileDescriptor) = HardLink(path().resolve(name).toString(), file)


    private fun verifyDuplicate(name: String) {
        if (nodes.any { it.name() == name }) {
            throw FileAlreadyExistsException("File with name $name already exists")
        }
    }
}

class WorkingDirectoryTree : WorkingDirectory {

    private val root: Directory = FSDirectory(HardLink("root", FileDescriptor.ROOT))
    private var cwd: Directory = root

    override fun create(path: Path, file: FileDescriptor): HardLink {
        val parent = getDir(path.parent())
        return parent.create(path.name(), file).value()
    }

    override fun mkdir(path: Path, file: FileDescriptor): HardLink {
        val parent = getDir(path.parent())
        return parent.mkdir(path.name(), file).value()
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
        return parent.remove(path.name()).value()
    }

    override fun rmdir(path: Path): HardLink {
        val dir = getDir(path)
        dir.delete()
        return dir.value()
    }

    private fun getDir(path: Path): Directory {
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
    fun parent(): Path = Path(elements.dropLast(1).joinToString("/"))

    private val elements: Array<String>;

    init {
        val clean = path.ifBlank { "." }

        elements = clean
            .replace(Regex("\\/\$"), "")
            .split(Regex("(?<!\\\\)\\/"))
            .toTypedArray()
    }

    fun slice(from: Int, to: Int? = null): Link {
        return Link(elements.slice(from..(to ?: elements.size)).joinToString("/"))
    }

    fun sliceLast(): Pair<Link, String> {
        // [this.slice(0, -1), this.elements.slice(-1)[0]]
        val lastElement = elements.last()
        val newElements = elements.dropLast(1)
        return Link(newElements.joinToString("/")) to lastElement
    }

    fun segments(): List<String> {
        return elements.map { it.replace(Regex("/\\\\(?=\\/)/g"), "") }
    }

    override fun toString(): String {
        return elements.joinToString("/").removePrefix("root")
    }

}