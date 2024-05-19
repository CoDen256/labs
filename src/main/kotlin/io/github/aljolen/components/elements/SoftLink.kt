package io.github.aljolen.components.elements

import io.github.aljolen.types.SoftLinkValue
import io.github.aljolen.types.TDirChild
import io.github.aljolen.types.TINodeOwner

data class SoftLink(
    override val name: String,
    override var parent: Dir?,
    override val node: Int,
) : TDirChild, TINodeOwner{
    var value: SoftLinkValue? = null
        private set

    fun removeValue(){
        this.value = null
    }

    fun remove(){
        this.parent?.removeChild(this)
        this.value?.removeSoftLink(this)
    }
}
