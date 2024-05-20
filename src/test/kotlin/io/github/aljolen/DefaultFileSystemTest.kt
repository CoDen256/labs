package io.github.aljolen

import io.github.aljolen.fs.FileType
import io.github.aljolen.fs.HardLink
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException
import java.nio.file.FileAlreadyExistsException


class DefaultFileSystemTest {

    lateinit var fs: DefaultFileSystem

    @BeforeEach
    fun setUp() {
        fs = DefaultFileSystem(4096 / 8)
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
    fun open() {
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