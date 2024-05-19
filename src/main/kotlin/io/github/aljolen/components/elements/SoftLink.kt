package io.github.aljolen.components.elements

import io.github.aljolen.types.SoftLinkValue
import io.github.aljolen.types.TDirChild
import io.github.aljolen.types.TINodeOwner

class SoftLink(
    override val name: String,
    v: SoftLinkValue,
    override val node: Int,
) : TDirChild, TINodeOwner{
    override var parent: Dir? = null;

    var value: SoftLinkValue? = v
        private set

    fun removeValue(){
        this.value = null
    }

    override fun remove(){
        this.parent?.removeChild(this)
        this.value?.removeSoftLink(this)
    }
}
