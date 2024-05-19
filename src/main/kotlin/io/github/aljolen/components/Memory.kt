package io.github.aljolen.components

import io.github.aljolen.components.elements.file.Block

class Memory {

    private val blocks = ArrayList<Block>()
    fun add(block: Block) {
        blocks.add(block)
    }

    fun get(index: Int): Block {
        return blocks.getOrNull(index) ?: throw Exception("No Block $index found")
    }

}