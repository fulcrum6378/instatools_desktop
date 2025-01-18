package ir.mahdiparastesh.instatools

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.Api.Companion.xFromSeconds
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.api.RelayPrefetchedStreamCache
import ir.mahdiparastesh.instatools.api.Rest
import java.io.File

class Downloader(private val api: Api) {
    private val queue = arrayListOf<Queued>()

    /** Detects download URLs of desired media via their official links. */
    suspend fun handlePostLink(link: String) {
        api.page(link, { status ->
            when (status) {
                302 -> System.err.println("Found redirection!")
                429 -> System.err.println("Too many requests!")
                else -> System.err.println("HTTP error code $status!")
            }
        }) { html ->
            val data = RelayPrefetchedStreamCache.crawl(html) { // hashMapOf<String, Map<String, Any>>()
                it.contains("PolarisPostRootQueryRelayPreloader")
            }
            if (System.getenv("debug") == "1")
                println("RelayPrefetchedStreamCache: " + data.keys.joinToString(", "))

            if ("PolarisPostRootQueryRelayPreloader" in data) {
                @Suppress("UNCHECKED_CAST")
                val medMap = (data["PolarisPostRootQueryRelayPreloader"]!!["items"] as List<Map<String, Any>>)[0]
                enqueue(link, Gson().fromJson(Gson().toJson(medMap), Media::class.java))
            } else if ("instagram://media?id=" in html) {
                val medId = html.substringAfter("instagram://media?id=").substringBefore("\"")
                if (System.getenv("debug") == "1")
                    println("Media ID: $medId")
                api.call<Rest.LazyList<Media>>(
                    Api.Endpoint.MEDIA_INFO.url.format(medId), Rest.LazyList::class,
                    typeToken = object : TypeToken<Rest.LazyList<Media>>() {}.type,
                    onError = { status, _ -> System.err.println("Error $status!") }
                ) { singleItemList -> enqueue(link, singleItemList.items.first()) }
            } else
                System.err.println("Shall we re-implement PageConfig?")
        }
    }

    /** Enqueues a media to be downloaded. */
    private suspend fun enqueue(link: String, med: Media) {
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
        download()
    }

    /** Downloads a media and saves it. */
    private suspend fun download() {
        val downloads = File("./downloads/")
        if (!downloads.isDirectory) downloads.mkdir()

        var fn: String
        queue.forEach { q ->
            fn = q.fileName()
            api.client.get(q.url!!).bodyAsChannel().copyAndClose(File(downloads, fn).writeChannel())
            println("Downloaded $fn")
        }
        queue.clear()
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
