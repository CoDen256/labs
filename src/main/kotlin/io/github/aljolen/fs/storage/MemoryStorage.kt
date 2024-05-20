package io.github.aljolen.fs.storage

class MemoryStorage(
    private val blockSize: Int,
    private val blocks: Int
) : Storage {

    private val storage = arrayOfNulls<Block>(blocks)

    override fun getBlockSize(): Int {
        return blockSize
    }

    override fun getBlocks(): Int {
        return blocks
    }

    override fun getOccupiedSize(): Int {
        return storage.mapNotNull { it?.getOccupiedSize() }.sum()
    }

    override fun getOccupiedBlocks(): Int {
        return storage.count { it != null }
    }

    override fun getBlock(id: Int): Block {
        return storage.get(id) ?: throw IllegalArgumentException("No block of id $id")
    }

    override fun newBlock(): Block {
        if (getOccupiedBlocks() == blocks) throw IllegalArgumentException("Max space of $blocks blocks exceeded")
        val id = storage.indexOfFirst { it == null }
        val block = DataBlock(blockSize, id)
        storage[id] = block
        return block
    }
}