package ir.mahdiparastesh.instatools.mod

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.api.RelayPrefetchedStreamCache
import ir.mahdiparastesh.instatools.api.Rest
import ir.mahdiparastesh.instatools.srv.Queuer

object Links {

    /** Resolves download URLs of desired posts or reels via their official links. */
    suspend fun handlePostLink(link: String, queuer: Queuer) {
        queuer.api.page(link, { status ->
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
                queuer.enqueue(link, Gson().fromJson(Gson().toJson(medMap), Media::class.java))
            } else if ("instagram://media?id=" in html) {
                val medId = html.substringAfter("instagram://media?id=").substringBefore("\"")
                if (System.getenv("debug") == "1")
                    println("Media ID: $medId")
                queuer.api.call<Rest.LazyList<Media>>(
                    Api.Endpoint.MEDIA_INFO.url.format(medId), Rest.LazyList::class,
                    typeToken = object : TypeToken<Rest.LazyList<Media>>() {}.type,
                    onError = { status, _ -> System.err.println("Error $status!") }
                ) { singleItemList -> queuer.enqueue(link, singleItemList.items.first()) }
            } else
                System.err.println("Shall we re-implement PageConfig?")
        }
    }
}
