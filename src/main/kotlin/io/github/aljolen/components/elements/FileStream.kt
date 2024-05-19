package io.github.aljolen.components.elements

import io.github.aljolen.components.elements.file.File
import io.github.aljolen.types.TINodeOwner

class FileStream(
    val file: File,
    override val node: Int
): TINodeOwner{
    var position: Int = 0
    set(value) {
        check(position >= 0) { "Position cannot be negative" }
        field = value
    }
}
