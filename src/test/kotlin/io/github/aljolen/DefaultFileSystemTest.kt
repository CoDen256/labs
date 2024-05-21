package io.github.aljolen

import io.github.aljolen.fs.DefaultFileSystem
import io.github.aljolen.fs.FileIO
import io.github.aljolen.fs.MemoryStorage
import io.github.aljolen.fs.api.FileDescriptor
import io.github.aljolen.fs.api.FileType
import io.github.aljolen.fs.api.HardLink
import io.github.aljolen.fs.api.StatInfo
import io.github.aljolen.fs.utils.StorageDisplay
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
        fs = DefaultFileSystem(FileIO(storage))
    }

    @Test
    fun create_ls() {

        assertThrows<FileNotFoundException> { fs.get(1) }
        assertFalse(fs.ls().isEmpty())

        val (name, file) = fs.create("test")
        val id = file.id

        assertEquals("/test", name)
        assertEquals(1, id)

        val (fdId, type, nlink, map) = fs.get(id)
        assertEquals(id, fdId)
        assertEquals(type, FileType.REGULAR)
        assertEquals(nlink, 1)
        assertTrue(map.isEmpty())

        val ls = fs.ls()
        assertEquals(2, ls.size)

        assertEquals(HardLink("/test", file), ls.get(1))
        assertEquals(2, fs.create("extra").id)
        assertEquals(3, fs.create("extra2").id)

        assertThrows<FileAlreadyExistsException> { fs.create("extra2") }
    }

    @Test
    fun link_unlink() {
        val new = fs.create("test")
        val link = fs.link("test", "test2")

        assertEquals("/test2", link.pathname)
        assertEquals(new.id, link.id)

        assertEquals(3, fs.ls().size) // extra .

        assertThrows<FileNotFoundException> { fs.link("test3", "test") }

        val (id, type, nlink, map) = fs.get(link.id)
        assertEquals(new.id, id)
        assertEquals(FileType.REGULAR, type)
        assertEquals(nlink, 2)


        fs.unlink("test")
        assertEquals(2, fs.ls().size)
        assertEquals(1, fs.get(link.id).nlink)
        assertEquals("/test2", fs.ls().get(1).pathname)
        assertEquals(1, fs.ls().get(1).id)

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
        assertEquals(StatInfo(1, FileType.REGULAR, blockSize * 3, 1, 0), stat)

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
    fun stat() {
        fs.create("test")
        val (name, fd) = fs.create("test2")
        fs.link("test2", "test3")

        val (id, type, size, nlink, nblock) = fs.stat("test3")
        assertEquals(fd.id, id)
        assertEquals(FileType.REGULAR, type)
        assertEquals(2, nlink)
        assertEquals(0, size)
        assertEquals(0, nblock)
    }


    @Test
    fun traverse() {
        val root = fs.ls()
        val rootFd = FileDescriptor(0, FileType.REGULAR, 0)
        assertEquals(HardLink("/.", rootFd), root.get(0))
        assertEquals(1, root.size)


        fs.create("test.dat")
        fs.truncate("test.dat", 10)
        val fd = fs.open("test.dat")
        fs.write(fd, 10, "abcdefghjijklmn".toByteArray())
        fs.close(fd)

        fs.link("test.dat", "test-copy.dat")
        fs.symlink("/././test.dat", "sy.dat")
        fs.mkdir("f")
        fs.mkdir("f/s")
        fs.create("f/s/extra.dat")
        fs.symlink("f/s/../../sy.dat", "one-more")

        val sym = fs.open("one-more")
        assertEquals( "abcdefghji"+'\u0000',fs.read(sym, 11))

        fs.cd("f")
        assertEquals("/f", fs.cwd())

        fs.cd("s")
        assertEquals("/f/s", fs.cwd())

        fs.cd(".")
        assertEquals("/f/s", fs.cwd())
        assertEquals(3, fs.ls().size)

        fs.cd("..")
        assertEquals("/f", fs.cwd())

        fs.cd(".././f/./s/./..")
        assertEquals("/f", fs.cwd())
        assertEquals(3, fs.ls().size)


        fs.link("s/extra.dat", "link")
        val out = fs.stat("link")
        assertEquals(FileType.REGULAR, out.type)
        fs.unlink("../f/link")
        fs.unlink("/f/s/extra.dat")
        
        fs.rmdir("/f/s")
        assertEquals(2, fs.ls().size)

    }

    @Test
    fun createAbs(){

        fs.cd("/")
        fs.mkdir("a")
        fs.cd("a")

        val link = fs.mkdir("/b")
        assertEquals("/b", link.pathname)
        assertEquals(2, fs.ls().size)

    }
}