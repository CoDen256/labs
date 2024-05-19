package io.github.aljolen.components.operations

import io.github.aljolen.components.elements.Dir
import io.github.aljolen.components.elements.HardLink
import io.github.aljolen.components.elements.SoftLink
import io.github.aljolen.components.elements.file.File
import io.github.aljolen.types.TDirChild
import io.github.aljolen.utils.Link

class Printer {

    fun getDirChildrenInfo(dir: Dir): String{
        return dir.getChildren()
            .joinToString("\n") {
            "${it.node} ${getOutputName(it)}"
            }
    }

    fun getFileInfo(file: File): String {
        return "Size : ${file.getSize()}, Blocks: ${file.getBlocks().size}"
    }

    fun getLinkName(link: TDirChild): String{
        return when(link){
            is SoftLink -> "soft link"
            is HardLink -> "hard link"
            else -> "unknown link"
        }
    }

    fun stat(link: TDirChild, file: File, ioBytesSize : Int, name: Link): String {
        return "Name: ${name}\n" +
                "${getFileInfo(file)}, " +
                "IO Block: ${ioBytesSize} " +
                "${getLinkName(link)}) " +
                if (link is HardLink) "" + link.file.hardLinks.size + " links" else ""
    }


    fun getOutputName(child: TDirChild): String{
        if (child !is SoftLink){return child.name}
        return child.value?.let {
            "${child.name} -> ${it.name}"
        } ?: "${child.name} -> (invalid)"
    }
}