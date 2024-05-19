package io.github.aljolen.components.factories

import io.github.aljolen.components.INodeCounter
import io.github.aljolen.components.elements.FileStream
import io.github.aljolen.components.elements.file.File

class FileStreamFactory(private val nodeCounter: INodeCounter) {
    fun create(file: File): FileStream {
        return FileStream(file, nodeCounter.next())
    }
}