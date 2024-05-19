package io.github.aljolen.components

class INodeCounter {
    private var current = 0;

    fun next(): Int {
        return current++
    }
}