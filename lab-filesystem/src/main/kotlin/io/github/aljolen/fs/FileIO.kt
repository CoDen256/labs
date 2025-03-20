package io.github.aljolen.fs

import io.github.aljolen.fs.api.*
import kotlin.math.abs

class FileIO(private val storage: Storage) : IO {
    private val fds = arrayOfNulls<FileStream>(256 * 4)

    private fun newStream(file: FileDescriptor): FileStream {
        val fd = nextNumericFd()
        val fileStream = FileStream(0, file, fd)
        fds[fd] = fileStream
        return fileStream
    }

    private fun nextNumericFd(): Int{
        for ((index, fileDescriptor) in fds.withIndex()){
            if (fileDescriptor == null){
                return index
            }
        }
        throw IllegalArgumentException("Max amount of file descriptors exceeded: ${fds.size}")
    }

    override fun open(file: FileDescriptor): Int {
        return newStream(file).fd
    }

    override fun stat(file: FileDescriptor): StatInfo {
        return file.let {
            StatInfo(
                it.id,
                it.type,
                it.size(),
                it.nlink,
                it.nblock()
            )
    }
        }

    override fun close(fd: Int) {
        fds[fd] = null
    }

    override fun seek(fd: Int, offset: Int) {
        val fileStream = getFileStream(fd)
        fileStream.offset = offset
    }


    override fun read(fd: Int, size: Int): ByteArray {
        val fileStream = getFileStream(fd)
        val file = fileStream.file

        if (size > file.size()){
            throw IllegalStateException("File size exceeded: ${file.size()}, but was: $size")
        }

        var current = fileStream.offset
        var length = size
        var result = ByteArray(0)

        for (blockNum in file.map) {
            if (length == 0) break
            val block = storage.getBlock(blockNum)

            if (current >storage.getBlockSize()) {
                current -= storage.getBlockSize()
                continue
            }

            val lengthInTheBlock = minOf(length, storage.getBlockSize() - current)

            result += block.read(current, current + lengthInTheBlock)
            length -= lengthInTheBlock
            current = 0
        }
        fileStream.offset += size
        return result
    }

    override fun write(fd: Int, size: Int, value: ByteArray) {
        val fileStream = getFileStream(fd)
        val file = fileStream.file

        if (fileStream.offset + size > file.size()){
            throw IllegalStateException("File size exceeded: ${file.size()}, but was: $size at offset ${fileStream.offset}")
        }

        val blocks = file
            .map
            .map { storage.getBlock(it) }
            .toMutableList()

        var offset = fileStream.offset
        var content = value.sliceArray(0 until size)

        for (block in blocks){
            if (content.isEmpty()) break
            if (offset > storage.getBlockSize()) {
                offset -= storage.getBlockSize()
                continue
            }

            val blockChunkSize = minOf(content.size, storage.getBlockSize() - offset)
            block.write(offset, content.sliceArray(0 until blockChunkSize))
            content = content.drop(blockChunkSize).toByteArray()
            offset = 0
        }

        fileStream.offset += size
    }

    override fun readSymlink(file: FileDescriptor): String{
        if (file.type != FileType.SYMBOLIC) throw IllegalArgumentException("$file is not a symbolic link")
        val block = storage.getBlock(file.map.first())
        return String(block.read(0, storage.getBlockSize())
            .dropLastWhile { it.toInt() == 0 }.toByteArray())
    }

    override fun writeSymlink(value: String, file: FileDescriptor) {
       truncate(file, storage.getBlockSize())
       val fd = open(file)
       write(fd, value.length, value.toByteArray())
       close(fd)
    }

    override fun truncate(file: FileDescriptor, size: Int) {
        val diff = size - file.size()
        if (diff == 0) return
        if (diff > 0) {
            addSize(diff, file)
            return
        }
        subSize(diff, file)
    }

    private fun getFileStream(fd: Int): FileStream {
        return fds[fd] ?: throw IllegalStateException("FD $fd is not open")
    }
    private fun FileDescriptor.size(): Int{
        return map.map { storage.getBlock(it) }.count() * storage.getBlockSize()
    }
    private fun FileDescriptor.nblock(): Int{
        return map.map { storage.getBlock(it) }.count { !it.isEmpty() }
    }

    private fun subSize(size: Int, fd: FileDescriptor) {
        val fullBlocksCount = abs(size / storage.getBlockSize())

        repeat(fullBlocksCount) {
            val index = fd.map.size - it - 1
            val blockId = fd.map.removeAt(index)
            storage.removeBlock(blockId)
        }
    }

    private fun addSize(size: Int, fd: FileDescriptor) {
        val fullBlocksCount = (size / storage.getBlockSize())
        val leftBlockSize = size % storage.getBlockSize()
        repeat(fullBlocksCount) {
            val block = storage.newBlock()
            fd.map.add(block.getId())
        }

        if (leftBlockSize != 0) {
            fd.map.add(storage.newBlock().getId())
        }
    }

}