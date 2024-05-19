package io.github.aljolen.components

class RangedSet<T> (
    private val max: Int
){
    private val links = HashSet<T>()

    fun add(link: T){
        check(links.size < max) {"Links size must be less than $max"}
        links.add(link)
    }

    fun getLinks() : Set<T> {
        return links
    }

}
