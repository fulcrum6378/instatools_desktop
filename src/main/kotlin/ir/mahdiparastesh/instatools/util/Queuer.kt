package ir.mahdiparastesh.instatools.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

abstract class Queuer<Item> {
    private var active = false
    private val queue = CopyOnWriteArrayList<Item>()
    protected abstract val outputDir: File?

    protected suspend fun enqueue(item: Item) {
        queue.add(item)
        if (!active) withContext(Dispatchers.IO) { start() }
    }

    private suspend fun start() {
        active = true
        if (outputDir?.isDirectory == false) outputDir?.mkdir()
        while (queue.isNotEmpty()) {
            handle(queue.first())
            queue.removeFirst()
        }
        active = false
    }

    protected abstract suspend fun handle(q: Item)
}
