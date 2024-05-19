package io.github.aljolen.components.operations

import io.github.aljolen.Finder
import io.github.aljolen.components.elements.Dir
import io.github.aljolen.components.elements.HardLink
import io.github.aljolen.components.elements.SoftLink
import io.github.aljolen.components.elements.file.File
import io.github.aljolen.types.SoftLinkValue
import io.github.aljolen.types.TDirChild
import io.github.aljolen.utils.Link

class Navigation(
    private var rootDir: Dir,
                 private var currentDir: Dir
) {
    private val finder: Finder = Finder()

    fun getDir(link: Link): Dir {
        val dir = getChild(link)

        if (dir is SoftLink) {
            val value = dir.value

            if (value !is Dir) {
                throw IllegalArgumentException("Path is not a dir")
            }

            return value
        }

        if (dir !is Dir) {
            throw IllegalArgumentException("Path is not a dir")
        }

        return dir
    }

    fun getHardLink(link: Link): HardLink {
        val hardLink = getChild(link)

        if (hardLink !is HardLink) {
            throw IllegalArgumentException("Path is not a hard link")
        }

        return hardLink
    }

    fun getLink(link: Link): TDirChild {
        val searchedLink = getChild(link)

        if (searchedLink !is HardLink && searchedLink !is SoftLink) {
            throw IllegalArgumentException("Path is not a hard or soft link")
        }

        return searchedLink
    }

    fun getHardLinkOrDir(link: Link): SoftLinkValue {
        val hardLinkOrDir = getChild(link)

        if (hardLinkOrDir is HardLink)return hardLinkOrDir
        if (hardLinkOrDir is Dir ) {return hardLinkOrDir}

        throw IllegalArgumentException("Path is not a dir or hard link")
    }

    fun getLinkWithFile(link: Link): Pair<TDirChild, File> {
        val searchedLink = getLink(link)

        if (searchedLink is SoftLink) {
            val value = searchedLink.value

            if (value !is HardLink) {
                throw IllegalArgumentException("Path is not a file")
            }

            return Pair(searchedLink, value.file)
        }

        return Pair(searchedLink, (searchedLink as HardLink).file)
    }

    fun setDir(link: Link): Unit {
        currentDir = getDir(link)
    }

    private fun getChild(link: Link): TDirChild {
        return finder.find(rootDir, currentDir, link)
    }


}