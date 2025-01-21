package ir.mahdiparastesh.instatools.util

import ir.mahdiparastesh.instatools.InvalidCommandException

abstract class LazyLister<Item> {
    protected val list: ArrayList<Item> = arrayListOf()
    protected var cursor: String? = null
    protected var index: Int = 1

    /** Remember always to call `super.fetch(reset)`. */
    open suspend fun fetch(reset: Boolean = false) {
        if (reset) {
            cursor = null
            index = 1
        }
        if (cursor == null) list.clear()
    }

    protected fun add(item: Item) {
        list.add(item)
        index++
    }

    protected fun endOfList() {
        cursor = null
        index = 1
        println("End of list.")
    }

    operator fun get(index: String): Item? = try {
        list[index.toInt() - 1]
    } catch (e: Exception) {
        throw InvalidCommandException("The number you entered is incorrect! (${e::class.simpleName})")
    }
}
