package ir.mahdiparastesh.instatools.util

import java.io.File

abstract class Queuer<Item> {
    private val queue = arrayListOf<Item>()
    protected abstract val outputDir: File?

    protected fun enqueue(item: Item) {
        queue.add(item)
    }

    fun start() {
        if (outputDir?.isDirectory == false) outputDir?.mkdir()
        while (queue.isNotEmpty()) {
            handle(queue.first())
            queue.removeFirst()
        }
    }

    protected abstract fun handle(q: Item)
}
