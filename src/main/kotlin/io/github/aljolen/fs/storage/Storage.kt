package io.github.aljolen.fs.storage

interface Storage {

    /** Block size in Bytes */
    fun getBlockSize(): Int

    /** Get total available number of blocks */
    fun getBlocksCount(): Int

    /** Get total occupied size in Bytes */
    fun getOccupiedSize(): Int;

    /** Total number of occupied blocks */
    fun getOccupiedBlocksCount(): Int

    /** Get block by its index */
    fun getBlock(id: Int): Block

    /** Create a new block of given size */
    fun newBlock(): Block

    /** Remove block by id */
    fun removeBlock(id: Int)
}

interface Block{

    /** Index of the block */
    fun getId(): Int

    /** Occupied block size */
    fun getOccupiedSize(): Int

    /** Whether the block is empty */
    fun isEmpty(): Boolean

    /** Write content at this offset */
    fun write(offset: Int, data: ByteArray)

    /** Read content between two boundaries */
    fun read(from: Int, to: Int): ByteArray
}