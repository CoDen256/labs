package io.github.aljolen.fs.storage

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MemoryStorageTest {

    @Test
    fun getBlockSize() {
        val storage = MemoryStorage(20, 10)

        assertEquals(20, storage.getBlockSize())
        assertEquals(10, storage.getBlocks())

        assertEquals(0, storage.getOccupiedSize())
        assertEquals(0, storage.getOccupiedBlocks())


        val newBlock = storage.newBlock()
        assertEquals(0, storage.getOccupiedSize())
        assertEquals(1, storage.getOccupiedBlocks())
        assertTrue(newBlock.isEmpty())

        assertEquals(0, newBlock.getId())

        newBlock.write(0, "hello".encodeToByteArray())

        assertEquals(5, storage.getOccupiedSize())
        assertEquals(1, storage.getOccupiedBlocks())
        val read = newBlock.read(0, 2)
        assertArrayEquals("he".toByteArray(), read)


        storage.getBlock(0).write(3, "hello".encodeToByteArray())
        assertArrayEquals(("helhello"+'\u0000'.toString().repeat(12)).toByteArray(), storage.getBlock(0).read(0, 20))
    }

}