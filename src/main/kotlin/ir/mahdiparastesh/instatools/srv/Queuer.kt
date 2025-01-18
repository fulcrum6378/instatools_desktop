package ir.mahdiparastesh.instatools.srv

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.Api.Companion.xFromSeconds
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

class Queuer(val api: Api) {
    private var active = false
    private val queue = CopyOnWriteArrayList<Queued>()
    private val downloads = File("./downloads/")

    init {
        if (!downloads.isDirectory) downloads.mkdir()
    }

    /** Enqueues a media to be downloaded. */
    suspend fun enqueue(link: String, med: Media) {
        if (med.carousel_media != null) for (car in med.carousel_media) queue.add(
            Queued(
                link,
                med.taken_at.xFromSeconds(),
                med.user!!.pk,
                med.user.username,
                car.pk,
                car.nearest(Media.BEST),
                car.thumb(),
                car.media_type.toInt().toByte(),
                med.caption!!.text
            )
        ) else queue.add(
            Queued(
                link,
                med.taken_at.xFromSeconds(),
                med.user!!.pk,
                med.user.username,
                med.pk,
                med.nearest(Media.BEST),
                med.thumb(),
                med.media_type.toInt().toByte(),
                med.caption!!.text
            )
        )

        if (!active) withContext(Dispatchers.IO) { download() }
    }

    /** Downloads a media and saves it. */
    private suspend fun download() {
        active = true
        var fn: String
        while (queue.isNotEmpty()) queue.first().also { q ->
            fn = q.fileName()
            api.client.get(q.url!!).bodyAsChannel().copyAndClose(File(downloads, fn).writeChannel())
            println("Downloaded $fn")
            queue.removeFirst()
        }
        active = false
    }

    /** Data structure for information of a media. */
    data class Queued(
        val link: String,
        var date: Long,
        var userId: String,
        var userName: String? = null,
        var itemId: String? = null,
        var url: String? = null,
        var thumb: String? = null,
        var mediaType: Byte,
        var caption: String? = null,
    ) {
        fun fileName() = "${userName}_${Utils.fileDateTime(date)}_" +
                "$itemId.${Media.Type.entries.find { it.inDb == mediaType }!!.ext}"
    }
}
