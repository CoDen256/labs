package io.github.aljolen.fs

import io.github.aljolen.utils.Link
import java.io.FileNotFoundException
import java.nio.file.FileAlreadyExistsException


interface Directory{
    fun create(path: Path, fd: Int): HardLink
    fun remove(path: Path): HardLink
    fun ls(): List<HardLink>
    fun get(path: Path): HardLink
    fun cwd(): HardLink
    fun cd(path: Path): HardLink
    fun path(): Path
}

class DefaultDirectory : Directory{
    private val links = ArrayList<HardLink>()

    override fun create(path: Path, fd: Int): HardLink {
        if (links.any{it.name == path.name()}){ throw FileAlreadyExistsException("File with name $path already exists") }
        val hardLink = HardLink(path.name(), fd)
        links.add(hardLink)
        return hardLink
    }

    override fun ls(): List<HardLink> {
        return links
    }

    override fun get(path: Path): HardLink {
        return links.find { it.name == path.name() }?: throw FileNotFoundException("File <{$path.name()}> Not Found")
    }

    override fun cwd(): HardLink {
        TODO("Not yet implemented")
    }

    override fun cd(path: Path): HardLink {
        TODO("Not yet implemented")
    }

    override fun path(): Path {
        TODO("Not yet implemented")
    }

    override fun remove(path: Path): HardLink {
        val link: HardLink = links.firstOrNull { it.name == path.name() } ?: throw FileNotFoundException("No link with name ${path.name()}")
        links.remove(link)
        return link
    }

}


class WorkingDirectoryTree() {


    fun ls(path: Path){

    }

//    fun

    fun change(path: Path){

    }

}

class Path(private val path: String){

    fun isRelative(): Boolean{
        return !path.startsWith("/")
    }

    fun name(): String = elements.last()

    private val elements: Array<String>;
    init {
        val clean = path.ifBlank { "." }

        elements = clean
            .replace(Regex("\\/\$"), "")
            .split(Regex("(?<!\\\\)\\/"))
            .toTypedArray()
    }

    fun slice(from: Int, to: Int?=null): Link {
        return Link(elements.slice(from..(to ?: elements.size)).joinToString("/"))
    }

    fun sliceLast(): Pair<Link, String> {
        // [this.slice(0, -1), this.elements.slice(-1)[0]]
        val lastElement = elements.last()
        val newElements = elements.dropLast(1)
        return Link(newElements.joinToString("/")) to lastElement
    }

    fun toArray(): List<String> {
        return elements.map { it.replace(Regex("/\\\\(?=\\/)/g"), "") }
    }

    override fun toString(): String {
        return elements.joinToString("/")
    }

}