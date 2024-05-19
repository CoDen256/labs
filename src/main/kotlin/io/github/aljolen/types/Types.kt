package io.github.aljolen.types

import io.github.aljolen.components.elements.Dir
import io.github.aljolen.components.elements.SoftLink

interface TDirChild{
    val name: String
    var parent: Dir?
}

interface TDir {
    val name: String
    val parent: TDir?
    val children: TDir
}

interface TDirParent {
    val dirParent: Dir?
}

interface SoftLinkValue{
    fun removeSoftLink(link: SoftLink)
}

interface THardLink

interface TINodeOwner{
    val node : Int
}
