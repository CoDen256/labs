package io.github.aljolen.fs.storage

interface Storage {

    /** Block size in Bytes */
    fun getBlockSize(): Int

    /** Get total size in Bytes */
    fun getOccupiedSize(): Int;

    fun getOccupiedBlocks(): Int

    /** Get total number of blocks */
    fun getBlocks(): Int

    fun getBlock(id: Int): Block

    fun newBlock(): Block
}

sealed interface Block{
    fun getId(): Int
    fun getOccupiedSize(): Int
    fun isEmpty(): Boolean
    fun write(offset: Int, data: ByteArray)
    fun read(from: Int, to: Int): ByteArray
}

class DataBlock(
    size: Int,
    private val id: Int,
    private var content: ByteArray=ByteArray(size)
): Block{
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