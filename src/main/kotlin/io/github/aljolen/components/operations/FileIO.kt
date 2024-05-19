package io.github.aljolen.components.operations

import io.github.aljolen.components.elements.file.Block
import io.github.aljolen.components.elements.file.File

class FileIO (private val blockSize: Int){


    fun read(file: File, from: Int, to: Int): String {
        var left = from
        var length = to - from
        var result = ""

        for (block in file.getBlocks()) {
            if (length == 0) break
            if (left > block.size) {
                left -= block.size
                println(left)
                continue
            }

            val lengthInTheBlock = minOf(length, block.size - left)

            result += block.read(left, left + lengthInTheBlock)
            length -= lengthInTheBlock
            left = 0
        }

        return result
    }

    fun write(file: File, from: Int, to: Int, value: String) {
        var left = from
        val blocks = file.getBlocks().toMutableList()
        var block = blocks.removeFirstOrNull()
        val fullValue = value.slice(0 until to).repeat(maxOf(1, to))

        while (block != null && fullValue.isNotEmpty()) {
            if (left > block.size) {
                left -= block.size
                continue
            }

            val blockSize = block.size
            val writeLength = blockSize - left
            val toWrite = fullValue.slice(0 until writeLength)

            fullValue.drop(writeLength)
            block.write(toWrite, left)

            left = 0
            block = blocks.removeFirstOrNull()
        }
    }

    fun truncate(file: File, size: Int) {
        val fullBlocksCount = (size / this.blockSize).toInt()
        val leftBlockSize = size % this.blockSize

        repeat(fullBlocksCount) { file.addBlock(Block(this.blockSize)) }

        if (leftBlockSize != 0) file.addBlock(Block(leftBlockSize))
    }

}