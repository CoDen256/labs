package io.github.aljolen.components.operations

import io.github.aljolen.components.INodeCounter
import io.github.aljolen.components.elements.FileStream
import io.github.aljolen.components.elements.file.File
import io.github.aljolen.components.factories.FileStreamFactory

class FileEdit(iNodeCounter: INodeCounter, private val fileIO: FileIO) {
    private val fileStreamFactory: FileStreamFactory = FileStreamFactory(iNodeCounter)
    private val openedFiles: MutableMap<String, FileStream> = mutableMapOf()

    fun open(file: File): Int {
        val fileStream = fileStreamFactory.create(file)
        val iNode = fileStream.node

        openedFiles[iNode.toString()] = fileStream

        return iNode
    }

    fun seek(iNode: Int, offset: Int) {
        isFileOpenedOrException(iNode)

        openedFiles[iNode.toString()]?.position = (offset)
    }

    fun read(iNode: Int, size: Int): String {
        isFileOpenedOrException(iNode)

        val stream = openedFiles[iNode.toString()]!!
        val position = stream.position

        return fileIO.read(stream.file, position, position + size)
    }

    fun write(iNode: Int, size: Int, value: String) {
        isFileOpenedOrException(iNode)

        val stream = openedFiles[iNode.toString()]!!
        val position = stream.position

        fileIO.write(stream.file, position, position + size, value)
    }

    fun close(iNode: Int) {
        isFileOpenedOrException(iNode)

        openedFiles.remove(iNode.toString())
    }

    private fun isFileOpenedOrException(iNode: Int) {
        if (!openedFiles.containsKey(iNode.toString())) {
            throw Exception("No opened file with this iNode")
        }
    }
}



