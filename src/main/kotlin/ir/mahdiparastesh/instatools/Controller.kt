package ir.mahdiparastesh.instatools

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.http.*
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.api.RelayPrefetchedStreamCache
import ir.mahdiparastesh.instatools.api.Rest
import ir.mahdiparastesh.instatools.job.Queuer
import ir.mahdiparastesh.instatools.util.Utils

class Controller(
    private val api: Api,
    private val queuer: Queuer
) {

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
                println(
                    "$savedIndex. ${i.media.link()} - @${i.media.owner().username} : " +
                            "${i.media.caption?.text?.replace("\n", " ")}"
                )
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
        println("Enter `s` again to load more posts...")
    }

    fun getSavedPost(index: String): Media? = try {
        savedPosts[index.toInt() - 1]
    } catch (e: Exception) {
        System.err.println("The number you entered is incorrect! (${e::class.simpleName})")
        null
    }

    /** Saves or unsaves posts. */
    suspend fun saveUnsave(med: Media, unsave: Boolean) {
        api.call<Rest.QuickResponse>(
            (if (unsave) Api.Endpoint.UNSAVE else Api.Endpoint.SAVE).url.format(med.pk),
            Rest.QuickResponse::class, HttpMethod.Post
        ) { rest ->
            if (rest.status == Utils.REST_STATUS_OK)
                println("Successfully ${if (unsave) "unsaved" else "saved"} ${med.link()}")
            else
                System.err.println("Couldn't ${if (unsave) "unsave" else "save"} this post!")
        }
    }

    /** Resolves download URLs of desired posts or reels via their official links. */
    suspend fun handlePostLink(link: String) {
        api.page(link) { html ->
            val data = RelayPrefetchedStreamCache.crawl(html) { // hashMapOf<String, Map<String, Any>>()
                it.contains("PolarisPostRootQueryRelayPreloader")
            }
            if (System.getenv("debug") == "1")
                println("RelayPrefetchedStreamCache: " + data.keys.joinToString(", "))

            if ("PolarisPostRootQueryRelayPreloader" in data) {
                @Suppress("UNCHECKED_CAST")
                val medMap = (data["PolarisPostRootQueryRelayPreloader"]!!["items"] as List<Map<String, Any>>)[0]
                queuer.enqueue(Gson().fromJson(Gson().toJson(medMap), Media::class.java), link)
            } else if ("instagram://media?id=" in html) {
                val medId = html.substringAfter("instagram://media?id=").substringBefore("\"")
                if (System.getenv("debug") == "1")
                    println("Media ID: $medId")
                api.call<Rest.LazyList<Media>>(
                    Api.Endpoint.MEDIA_INFO.url.format(medId), Rest.LazyList::class,
                    typeToken = object : TypeToken<Rest.LazyList<Media>>() {}.type,
                ) { singleItemList ->
                    queuer.enqueue(singleItemList.items.first(), link)
                }
            } else
                System.err.println("Shall we re-implement PageConfig?")
        }
    }
}
