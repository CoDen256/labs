package io.github.aljolen.fs

import io.github.aljolen.fs.api.Block
import io.github.aljolen.fs.api.Storage
import java.util.*

class MemoryStorage(
    private val blockSize: Int,
    private val blockCount: Int
) : Storage {
    private val storage = arrayOfNulls<Block>(blockCount)

    override fun getBlockSize(): Int = blockSize

    override fun getBlocksCount(): Int = blockCount

    override fun getOccupiedSize(): Int {
        return storage.mapNotNull { it?.getOccupiedSize() }.sum()
    }

    override fun getOccupiedBlocksCount(): Int {
        return storage.count { it != null }
    }

    override fun getBlock(id: Int): Block {
        return storage.get(id) ?: throw IllegalArgumentException("No block of id $id")
    }

    override fun newBlock(): Block {
        if (getOccupiedBlocksCount() == blockCount) throw IllegalArgumentException("Max number of $blockCount blocks exceeded")
        val id = nextFreeBlockIndex()
        val block = MemoryBlock(blockSize, id)
        storage[id] = block
        return block
    }

    override fun removeBlock(id: Int) {
        storage[id] = null
    }

    override fun getStorageBitMap(): BitSet {
        return BitSet.valueOf(storage.map { it?.let { 1L } ?: 0L}.toLongArray())
    }

    private fun nextFreeBlockIndex() = storage.indexOfFirst { it == null }
}

class MemoryBlock(
    size: Int,
    private val id: Int,
): Block {

    private val content: ByteArray=ByteArray(size)

    override fun getId(): Int {
        return id
    }

    override fun getOccupiedSize(): Int {
        return content.count { it != 0.toByte()}
    }

    override fun isEmpty(): Boolean {
        return getOccupiedSize() == 0
    }

    override fun write(offset: Int, data: ByteArray) {
        data.copyInto(content, offset)
    }

    override fun read(from: Int, to: Int): ByteArray {
        return content.copyOfRange(from, to)
    }
}