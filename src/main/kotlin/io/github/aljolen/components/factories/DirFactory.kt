package io.github.aljolen.components.factories

import io.github.aljolen.components.INodeCounter
import io.github.aljolen.components.elements.Dir

class DirFactory(private val nodeCounter: INodeCounter) {
    fun create(name: String): Dir {
        return Dir(name, nodeCounter.next())
    }
}