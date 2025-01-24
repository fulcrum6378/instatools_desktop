package ir.mahdiparastesh.instatools.util

import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

abstract class Queuer<Item> {
    private var thread: Thread? = null
    private val queue = CopyOnWriteArrayList<Item>()
    protected abstract val outputDir: File?

    protected fun enqueue(item: Item) {
        queue.add(item)
        if (thread == null) {
            thread = Thread { start() }
            thread?.start()
        }
    }

    private fun start() {
        if (outputDir?.isDirectory == false) outputDir?.mkdir()
        while (queue.isNotEmpty()) {
            handle(queue.first())
            queue.removeFirst()
        }
        thread?.interrupt()
        thread = null
    }

    protected abstract fun handle(q: Item)
}
