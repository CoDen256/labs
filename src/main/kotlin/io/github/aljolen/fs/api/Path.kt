package io.github.aljolen.fs.api

class Path(private val path: String) {

    fun resolve(subPath: String): Path {
        return Path(this.path + "/" + subPath)
    }

    fun isRelative(): Boolean {
        return !path.startsWith("/")
    }

    fun name(): String = elements.last()
    fun parent(): Path? {
        if (elements.size == 1) {
            return null
        }
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
            .filter { it.isNotBlank() }
    }

    override fun toString(): String {
        return elements.joinToString("/").removePrefix("root")
    }

}