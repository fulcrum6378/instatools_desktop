package ir.mahdiparastesh.instatools

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.api.RelayPrefetchedStreamCache
import ir.mahdiparastesh.instatools.api.Rest
import ir.mahdiparastesh.instatools.srv.Queuer

class Controller(private val api: Api) {
    private val queuer = Queuer(api)

    private val savedPosts = arrayListOf<Media>()
    private var savedMaxId: String? = null
    private var savedIndex = 1

    suspend fun listSavedPosts(reset: Boolean = false) {
        if (reset) {
            savedIndex = 1
            savedPosts.clear()
        }
        api.call<Rest.LazyList<Rest.SavedItem>>(
            Api.Endpoint.SAVED.url + (if (savedMaxId != null && !reset) "?max_id=$savedMaxId" else ""),
            Rest.LazyList::class, typeToken = object : TypeToken<Rest.LazyList<Rest.SavedItem>>() {}.type
        ) { lazyList ->
            for (i in lazyList.items) {
                println("$savedIndex: @${i.media.owner?.username} ${i.media.caption?.text}")
                savedPosts.add(i.media)
                savedIndex++
            }
            if (lazyList.more_available)
                savedMaxId = lazyList.next_max_id
            else {
                savedMaxId = null
                savedIndex = 1
            }
        }
    }

    /** Resolves download URLs of desired posts or reels via their official links. */
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
                queuer.enqueue(link, Gson().fromJson(Gson().toJson(medMap), Media::class.java))
            } else if ("instagram://media?id=" in html) {
                val medId = html.substringAfter("instagram://media?id=").substringBefore("\"")
                if (System.getenv("debug") == "1")
                    println("Media ID: $medId")
                api.call<Rest.LazyList<Media>>(
                    Api.Endpoint.MEDIA_INFO.url.format(medId), Rest.LazyList::class,
                    typeToken = object : TypeToken<Rest.LazyList<Media>>() {}.type,
                ) { singleItemList -> queuer.enqueue(link, singleItemList.items.first()) }
            } else
                System.err.println("Shall we re-implement PageConfig?")
        }
    }

}
