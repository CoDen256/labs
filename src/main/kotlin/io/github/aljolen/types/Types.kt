package io.github.aljolen.types

import io.github.aljolen.components.elements.Dir
import io.github.aljolen.components.elements.SoftLink

interface TDirChild: TINodeOwner{
    val name: String
    var parent: Dir?
    fun remove()
}

interface TDir {
    val name: String
    val parent: TDir?
    val children: TDir
}

interface TDirParent {
    val dirParent: Dir?
}

interface SoftLinkValue: TDirChild{
    fun removeSoftLink(link: SoftLink)
}

interface ILink

interface THardLink

interface TINodeOwner{
    val node : Int
}
