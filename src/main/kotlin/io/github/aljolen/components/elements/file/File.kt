package io.github.aljolen.components.elements.file

import io.github.aljolen.components.elements.HardLink

class File {
    val blocks: HashSet<Block> = HashSet()
    val hardLinks: HashSet<HardLink> = HashSet()

    fun addBlock(block: Block) {
        this.blocks.add(block)
    }

    fun addHardLink(hardLink: HardLink) {
        this.hardLinks.add(hardLink)
    }

    fun removeBlock(block: Block) {
        this.blocks.remove(block)
    }

    fun removeHardLink(hardLink: HardLink) {
        this.hardLinks.remove(hardLink)
    }

    fun getBlocks(): Set<Block> {
        return blocks
    }

    fun getHardLinks(): Set<HardLink> {
        return hardLinks
    }

    fun getSize(): Int {
        return blocks.sumOf { it.size }
    }

}
