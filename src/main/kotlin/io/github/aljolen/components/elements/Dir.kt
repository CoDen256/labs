package io.github.aljolen.components.elements

import io.github.aljolen.types.SoftLinkValue
import io.github.aljolen.types.TDirChild
import io.github.aljolen.types.TINodeOwner

data class Dir(
    override val name: String,
    override val node: Int,
    override var parent: Dir? = null,
) : TINodeOwner, TDirChild, SoftLinkValue {
    private val children = HashSet<TDirChild>()
    private val softLinks = HashSet<SoftLink>()

    fun getChildren(): Set<TDirChild> {
        return HashSet(this.children)
    }

    fun getSoftLinks(): Set<SoftLink> {
        return HashSet(this.softLinks)
    }

    fun addSoftLink(softLink: SoftLink){
        this.softLinks.add(softLink)
    }

    override fun removeSoftLink(link: SoftLink){
        this.softLinks.remove(link)
    }

    fun removeChild(child: TDirChild){
        this.children.remove(child)
        child.parent = null
    }

    fun add(child: TDirChild){
        check(children.none{ it.name == child.name }) {"${child.name} already exists"}

        child.parent = this
        children.add(child)
    }

    override fun remove(){
        parent?.removeChild(this)
        softLinks.forEach { it.removeValue() }
    }
}