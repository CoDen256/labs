package io.github.aljolen.fs.api

import java.io.FileNotFoundException
import java.nio.file.FileAlreadyExistsException



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
    fun ls(): List<HardLink>
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


class FSDirectory(
    private val link: HardLink,
    private val parent: Directory?=null
) : Directory {

    private val nodes = HashMap<HardLink, Node>()

    init {
        newNode(newHardLink(".", link.file), this)
        if (parent != null){
            newNode(newHardLink("..", parent.value().file), parent)
        }
    }

    override fun value(): HardLink = link

    override fun create(name: String, file: FileDescriptor): HardLink {
        verifyDuplicate(name)
        val link = newHardLink(name, file)
        newNode(link, FSFile(link))
        return link
    }

    private fun newNode(link: HardLink, node: Node) {
        nodes[link] = node
    }

    override fun mkdir(name: String, file: FileDescriptor): HardLink {
        verifyDuplicate(name)
        val link = newHardLink(name, file)
        newNode(link, FSDirectory(link, this))
        return link
    }

    override fun remove(name: String): HardLink {
        val link = getLink(name)
        if (link.file.type == FileType.DIRECTORY) {throw IllegalArgumentException("$name is a directory")}
        nodes.remove(link)
        link.file.nlink--
        return link
    }

    private fun getLink(name: String): HardLink {
        return nodes.keys.find { Path(it.pathname).name() == name } ?: throw FileNotFoundException("File with name $name does not exist")
    }

    override fun get(name: String): Node {
        if (name == ".") return this
        if (name == "..") return parent ?: throw FileNotFoundException("Directory with name $name does not exist")
        return nodes[getLink(name)] ?: throw FileNotFoundException("Hardlink $name not found")
    }

    override fun ls(): List<HardLink> {
        return nodes.map { it.key }
    }

    override fun delete() {
        link.file.nlink--
//        for (node in links) {
//            if (node is Directory) {node.delete()}
//            else remove(node.name())
//        }
    }

    override fun path(): Path {
        return Path(link.pathname)
    }

    override fun fd(): Int {
        return link.id
    }

    override fun nodes(): List<Node> {
        return nodes.values.toList()
    }

    override fun name(): String {
        return path().name()
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
        return cwd.ls()
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