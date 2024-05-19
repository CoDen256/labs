package io.github.aljolen.components.factories

import io.github.aljolen.components.INodeCounter
import io.github.aljolen.components.elements.SoftLink
import io.github.aljolen.types.SoftLinkValue

class SoftLinkFactory(private val nodeCounter: INodeCounter)  {

    fun create(name: String, value: SoftLinkValue): SoftLink{
        return SoftLink(name, value, nodeCounter.next())
    }
}