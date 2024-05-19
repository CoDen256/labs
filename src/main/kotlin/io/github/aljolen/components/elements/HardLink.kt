package io.github.aljolen.components.elements

import io.github.aljolen.components.elements.file.File
import io.github.aljolen.types.SoftLinkValue
import io.github.aljolen.types.TDirChild
import io.github.aljolen.types.TINodeOwner

data class HardLink(
    val file: File,
    override val name: String,
    override val node: Int,
    override var parent: Dir?=null,
): TINodeOwner, TDirChild, SoftLinkValue {

    private val softLinks: MutableSet<SoftLink> = HashSet()

    fun addSoftLink(softLink: SoftLink) {
        softLinks.add(softLink)
    }

    override fun removeSoftLink(link: SoftLink) {
        softLinks.remove(link)
    }

    fun getSoftLinks() = HashSet(softLinks)

    override fun remove(){
        parent?.removeChild(this)
        file.removeHardLink(this)
        softLinks.forEach { it.removeValue() }
    }
}
