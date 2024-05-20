package io.github.aljolen

import io.github.aljolen.fs.FileType
import io.github.aljolen.fs.HardLink
import io.github.aljolen.fs.StatInfo
import io.github.aljolen.fs.storage.MemoryStorage
import io.github.aljolen.utils.StorageDisplay
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException
import java.nio.file.FileAlreadyExistsException


class DefaultFileSystemTest {

    val blockCount = 8
    val blockSize = 16

    lateinit var fs: DefaultFileSystem
    lateinit var storage: MemoryStorage
    @BeforeEach
    fun setUp() {
        storage = MemoryStorage(blockSize, blockCount)
        fs = DefaultFileSystem(storage)
    }

    @Test
    fun create_ls() {

        assertThrows<FileNotFoundException> { fs.get(0) }
        assertTrue(fs.ls().isEmpty())

        val (name, id) = fs.create("test")

        assertEquals("test", name)
        assertEquals(id, 0)

        val (fdId, type, nlink, map) = fs.get(id)
        assertEquals(id, fdId)
        assertEquals(type, FileType.REGULAR)
        assertEquals(nlink, 1)
        assertTrue(map.isEmpty())

        val ls = fs.ls()
        assertEquals(1, ls.size)

        assertEquals(HardLink("test", 0), ls.first())
        assertEquals(1, fs.create("extra").id)
        assertEquals(2, fs.create("extra2").id)

        assertThrows<FileAlreadyExistsException> { fs.create("extra2") }
    }

    @Test
    fun link_unlink() {
        val new = fs.create("test")
        val link = fs.link("test", "test2")

        assertEquals("test2", link.name)
        assertEquals(new.id, link.id)

        assertEquals(2, fs.ls().size)

        assertThrows<FileNotFoundException> { fs.link("test3", "test") }

        val (id, type, nlink, map) = fs.get(link.id)
        assertEquals(new.id, id)
        assertEquals(FileType.REGULAR, type)
        assertEquals(nlink, 2)


        fs.unlink("test")
        assertEquals(1, fs.ls().size)
        assertEquals(1, fs.get(link.id).nlink)
        assertEquals("test2", fs.ls().first().name)
        assertEquals(0, fs.ls().first().id)

        assertThrows<FileNotFoundException> { fs.unlink("test") }
    }

    @Test
    fun truncate_open_seek_read_write_read_close() {
        fs.create("test")

        assertThrows<IllegalStateException> { fs.read(0, 10) }
        val invalid = fs.open("test")
        fs.close(invalid)
        assertThrows<IllegalStateException> { fs.read(invalid, 10) }

        assertThrows<IllegalStateException> { fs.read(fs.open("test"), 10) }


        fs.truncate("test", blockSize * 2 + 1)
        val stat = fs.stat("test")
        assertEquals(StatInfo(0, FileType.REGULAR, blockSize * 3, 1, 0), stat)

        val fd = fs.open("test")
        assertArrayEquals(ByteArray(10), fs.read(fd, 10))

        fs.seek(fd, 1)
        fs.write(fd, 10, "0123456789".toByteArray())
        fs.write(fd, 10, "abcdefghij".toByteArray())
        fs.seek(fd, 32)
        fs.write(fd, 3, "HEL".toByteArray())
        // '_0123456789abcde|fghij___________|HEL______________'
        assertEquals(3, fs.stat("test").nblock)

        fs.seek(fd, 0)

        assertEquals('\u0000' + "0123", fs.read(fd, 5))
        assertEquals("45678", fs.read(fd, 5))
        fs.seek(fd, 16)
        assertEquals("fghij"+'\u0000', fs.read(fd, 6))
        fs.seek(fd, 32)
        assertEquals("HEL"+'\u0000'.toString().repeat(blockSize-3), fs.read(fd, blockSize))


        fs.create("extra")
        fs.truncate("extra", blockSize * 2)

        fs.truncate("test", blockSize*4+3) // added 2 blocks
        assertEquals(3, fs.stat("test").nblock)
        fs.seek(fd, blockSize*3)
        fs.write(fd, 21, "abcdefghijklmnopqrstu".toByteArray())
        assertEquals(5, fs.stat("test").nblock)
        assertEquals(5*blockSize, fs.stat("test").size)

        fs.truncate("extra", 1)
        assertEquals(blockSize, fs.stat("extra").size)

        fs.seek(fd, blockSize*4)
        assertEquals("qrstu"+'\u0000', fs.read(fd, 6))

        println(StorageDisplay().display(storage))
    }

    private fun assertEquals(out: String, read: ByteArray) {
        assertArrayEquals(out.toByteArray(), read)
    }

    @Test
    fun close() {
    }

    @Test
    fun seek() {
    }

    @Test
    fun read() {
    }

    @Test
    fun write() {
    }

    @Test
    fun truncate() {
    }

    @Test
    fun stat() {
        fs.create("test")
        val (name, id0) = fs.create("test2")
        fs.link("test2", "test3")

        val (id, type, size, nlink, nblock) = fs.stat("test3")
        assertEquals(id0, id)
        assertEquals(FileType.REGULAR, type)
        assertEquals(2, nlink)
        assertEquals(0, size)
        assertEquals(0, nblock)
    }



    @Test
    fun symlink() {
    }

    @Test
    fun mkdir() {
    }

    @Test
    fun unlink() {
    }

    @Test
    fun rmdir() {
    }

    @Test
    fun cd() {
    }


    @Test
    fun testStat() {
    }

    @Test
    fun testLs() {
    }

    @Test
    fun testCreate() {
    }

    @Test
    fun testOpen() {
    }

    @Test
    fun testOpen1() {
    }

    @Test
    fun testClose() {
    }

    @Test
    fun testSeek() {
    }

    @Test
    fun testRead() {
    }

    @Test
    fun testWrite() {
    }

    @Test
    fun testLink() {
    }

    @Test
    fun testUnlink() {
    }

    @Test
    fun testTruncate() {
    }
}