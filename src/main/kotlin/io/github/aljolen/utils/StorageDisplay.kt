package io.github.aljolen.utils

import io.github.aljolen.fs.storage.Block
import io.github.aljolen.fs.storage.Storage

class StorageDisplay {
    fun display(storage: Storage): String {
        val occupied = storage
            .getStorageBitMap()
            .toLongArray()
            .dropLastWhile { it == 0L }

        return List(occupied.size) { index ->
            if (isOccupied(occupied, index)) {
                storage.getBlock(index)
            } else {
                null
            }
        }.withIndex().joinToString("\n") {
            displayBlock(it, storage)
        }
    }

    private fun isOccupied(occupied: List<Long>, index: Int) = occupied.get(index) == 1L

    private fun displayBlock(iVal: IndexedValue<Block?>, storage: Storage): String {
        val index = iVal.index.toString().padStart(2, '0')
        val block = iVal.value ?: return "${index}|<null>|"
        val content = String(block.read(0, storage.getBlockSize()))
        return "$index|$content|"
    }
}