package io.github.aljolen.components.factories

import io.github.aljolen.components.INodeCounter
import io.github.aljolen.components.elements.HardLink
import io.github.aljolen.components.elements.file.File

class HardLinkFactory(private val nodeCounter: INodeCounter)  {

    fun create(name : String): HardLink {
        return  HardLink(File(), name, nodeCounter.next())
    }

    fun duplicate(name: String, hardLink: HardLink): HardLink {
        return HardLink(hardLink.file, name, hardLink.node)
    }
}