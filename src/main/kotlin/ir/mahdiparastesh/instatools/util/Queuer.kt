package ir.mahdiparastesh.instatools.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

abstract class Queuer<Item> {
    private var active = false
    private val queue = CopyOnWriteArrayList<Item>()
    protected abstract val outputDir: File?

    protected fun enqueue(item: Item) {
        queue.add(item)
        if (!active) CoroutineScope(Dispatchers.IO).launch { start() }
    }

    private fun start() {
        active = true
        if (outputDir?.isDirectory == false) outputDir?.mkdir()
        while (queue.isNotEmpty()) {
            handle(queue.first())
            queue.removeFirst()
        }
        active = false
    }

    protected abstract fun handle(q: Item)
}
