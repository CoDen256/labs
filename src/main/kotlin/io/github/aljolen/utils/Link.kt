package io.github.aljolen.utils

class Link(
    value: String
){
    private val elements: Array<String>;
    init {
        val clean = value.ifBlank { "." }

        elements = clean
            .replace(Regex("\\/\$"), "")
            .split(Regex("(?<!\\\\)\\/"))
            .toTypedArray()
    }

    fun slice(from: Int, to: Int?=null): Link{
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