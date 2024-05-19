package io.github.aljolen

import io.github.aljolen.components.elements.Dir
import io.github.aljolen.components.elements.SoftLink
import io.github.aljolen.types.TDirChild
import io.github.aljolen.utils.Link

class Finder {
    fun find(rootDir: Dir, source: Dir, path: Link): TDirChild {
        val elements = ArrayList(path.toArray())
        var result: TDirChild? = source

        while (elements.isNotEmpty()) {
            val element = elements.removeFirst()
            result = if (result is SoftLink) result.value else result
            if (result !is Dir) throw IllegalArgumentException("'${result?.name}' is not a dir")

            when (element) {
                "." -> continue
                "" -> result = rootDir
                ".." -> {
                    val parent: Dir? = result.parent
                    return parent ?: result
                }
                else -> {
                    val resultOrUndefined: TDirChild? = result.getChildren().find { child -> child.name == element }
                    if (resultOrUndefined == null) {
                        throw IllegalArgumentException("No dir or file with name '$element'")
                    }
                    result = resultOrUndefined
                }
            }
        }

        return result ?: throw IllegalArgumentException("No dir or file found")
    }


}