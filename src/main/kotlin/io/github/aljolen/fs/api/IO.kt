package io.github.aljolen.fs.api

interface IO {
    fun open(file: FileDescriptor): Int
    fun truncate(file: FileDescriptor, size: Int)
    fun stat(file: FileDescriptor): StatInfo
    fun close(fd: Int)
    fun seek(fd: Int, offset: Int)
    fun read(fd: Int, size: Int): ByteArray
    fun write(fd: Int, size: Int, value: ByteArray)
}

