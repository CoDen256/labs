package io.github.aljolen.fs.storage

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MemoryStorageTest {

    @Test
    fun getBlockSize() {
        val storage = MemoryStorage(20, 10)

        assertEquals(20, storage.getBlockSize())
        assertEquals(10, storage.getBlocksCount())

        assertEquals(0, storage.getOccupiedSize())
        assertEquals(0, storage.getOccupiedBlocksCount())


        val newBlock = storage.newBlock()
        assertEquals(0, storage.getOccupiedSize())
        assertEquals(1, storage.getOccupiedBlocksCount())

        assertTrue(newBlock.isEmpty())
        assertEquals(0, newBlock.getId())
        assertEquals(0, newBlock.getOccupiedSize())

        newBlock.write(0, "hello".encodeToByteArray())
        assertEquals(5, storage.getOccupiedSize())
        assertEquals(5, newBlock.getOccupiedSize())

        val content = newBlock.read(0, 2)
        assertArrayEquals("he".toByteArray(), content)

        val block = storage.getBlock(0)
        block.write(3, "hello".encodeToByteArray())
        assertArrayEquals(("helhello"+'\u0000'.toString().repeat(12)).toByteArray(), block.read(0, 20))
    }
}