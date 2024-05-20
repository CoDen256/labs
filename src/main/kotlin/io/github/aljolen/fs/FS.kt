package io.github.aljolen.fs

interface FS {

    /** Output information about a file (file descriptor data). */
    fun stat(name: String): StatInfo


    /** Output a list of hard links to files with file descriptor numbers in a directory. */
    fun ls(): List<HardLink>

    /**
     * Create a regular file and create a hard link named name to it in the
     * directory.
     */
    fun create(name: String): HardLink


    /**
     * Open a regular file pointed to by the hard link named name. The command
     * must assign the smallest free integer number (let’s call it the
     * “numerical file descriptor”) to work with the open file (this integer
     * number is not the same as the file descriptor number in the FS). One
     * file can be opened several times. The number of numeric file descriptors
     * can be limited
     */
    fun open(name: String): Int

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
    fun seek(fd: Int, offset: Long)

    /**
     * Read size bytes of data from an open file, size is added to the offset
     * value.
     */
    fun read(fd: Int, size: Long)

    /**
     * Write size bytes of data to an open file, size is added to the offset
     * value.
     */
    fun write(fd: Int, size: Long, value: ByteArray)

    /**
     * Create a hard link named name2 to the file pointed to by the hard link
     * named name1
     */
    fun link(name1: String, name2: String): HardLink

    /** Remove the hard link named name */
    fun unlink(name: String)

    /**
     * Change the size of the file pointed to by the hard link named name. If
     * the file size increases, then the uninitialized data is zero.
     */
    fun truncate(name: String, size: Long)
}

data class StatInfo(
    val id: Int,
    val type: FileType,
    val size: Int,
    val nlink: Int,
    val nblock: Int
)

data class HardLink(
    val name: String,
    val id: Int
)

data class FileDescriptor(
    val id: Int,
    val type: FileType,
    var nlink: Int,
    val map: MutableList<Int> = ArrayList()
) {
    val size: Int
        get() = map.size

    val nblock: Int
        get() = map.size
}

enum class FileType {
    REGULAR, DIRECTORY
}



