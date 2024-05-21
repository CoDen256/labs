package io.github.aljolen.fs.api

interface FS {

    /** Output information about a file (file descriptor data). */
    fun stat(pathname: String): StatInfo


    /**
     * Output a list of hard links to files with file descriptor numbers in a
     * directory.
     */
    fun ls(): List<HardLink>

    /**
     * Create a regular file and create a hard link named name to it in the
     * directory.
     */
    fun create(pathname: String): HardLink


    /**
     * Open a regular file pointed to by the hard link named name. The command
     * must assign the smallest free integer number (let’s call it the
     * “numerical file descriptor”) to work with the open file (this integer
     * number is not the same as the file descriptor number in the FS). One
     * file can be opened several times. The number of numeric file descriptors
     * can be limited
     */
    fun open(pathname: String): Int

    /**
     * Close previously opened file with numeric file descriptor fd, fd number
     * becomes free.
     */
    fun close(fd: Int)

    /**
     * Specify the offset for the open file where the next read or write will
     * begin (hereinafter “offset”). The file just opened has zero offset. This
     * offset is specified for this fd only.
     */
    fun seek(fd: Int, offset: Int)

    /**
     * Read size bytes of data from an open file, size is added to the offset
     * value.
     */
    fun read(fd: Int, size: Int): ByteArray

    /**
     * Write size bytes of data to an open file, size is added to the offset
     * value.
     */
    fun write(fd: Int, size: Int, value: ByteArray)

    /**
     * Create a hard link named name2 to the file pointed to by the hard link
     * named name1
     */
    fun link(pathname1: String, pathname2: String): HardLink

    /** Remove the hard link named name */
    fun unlink(pathname: String)

    /**
     * Change the size of the file pointed to by the hard link named name. If
     * the file size increases, then the uninitialized data is zero.
     */
    fun truncate(pathname: String, size: Int)


    fun mkdir(pathname: String): HardLink

    fun rmdir(pathname: String): HardLink

    fun cd(pathname: String): HardLink

    fun cwd(): String

    fun symlink(value: String, pathname: String)
}

data class StatInfo(
    val id: Int,
    val type: FileType,
    val size: Int,
    val nlink: Int,
    val nblock: Int
)

data class HardLink(
    val pathname: String,
    val file: FileDescriptor
) {
    val id: Int
        get() = file.id
}

data class FileDescriptor(
    val id: Int,
    val type: FileType,
    var nlink: Int,
    val map: MutableList<Int> = ArrayList()
){
    companion object{
        val ROOT = FileDescriptor(-1, FileType.DIRECTORY, 0)
    }
}

class FileStream(var offset: Int, val file: FileDescriptor, val fd: Int)

enum class FileType {
    REGULAR, DIRECTORY
}



