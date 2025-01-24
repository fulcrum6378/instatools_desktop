package ir.mahdiparastesh.instatools.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.Context.downloader
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.api.RelayPrefetchedStreamCache
import ir.mahdiparastesh.instatools.api.Rest

object SimpleTasks {

    /** Resolves download URLs of desired posts or reels via their official links. */
    fun handlePostLink(link: String, idealSize: Float) {
        val html = api.page(link)
        val data = RelayPrefetchedStreamCache.crawl(html) { // hashMapOf<String, Map<String, Any>>()
            it.contains("PolarisPostRootQueryRelayPreloader")
        }
        if (System.getenv("debug") == "1")
            println("RelayPrefetchedStreamCache: " + data.keys.joinToString(", "))

        if ("PolarisPostRootQueryRelayPreloader" in data) {
            @Suppress("UNCHECKED_CAST")
            val medMap = (data["PolarisPostRootQueryRelayPreloader"]!!["items"] as List<Map<String, Any>>)[0]
            downloader.download(Gson().fromJson(Gson().toJson(medMap), Media::class.java), idealSize, link)
        } else if ("instagram://media?id=" in html) {
            val medId = html.substringAfter("instagram://media?id=").substringBefore("\"")
            if (System.getenv("debug") == "1")
                println("Media ID: $medId")
            val singleItemList = api.call<Rest.LazyList<Media>>(
                Api.Endpoint.MEDIA_INFO.url.format(medId), Rest.LazyList::class,
                typeToken = object : TypeToken<Rest.LazyList<Media>>() {}.type,
            )
            downloader.download(singleItemList.items.first(), idealSize, link)
        } else
            System.err.println("Shall we re-implement PageConfig?")
    }
}
