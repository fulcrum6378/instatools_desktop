package ir.mahdiparastesh.instatools.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.Context.downloader
import ir.mahdiparastesh.instatools.api.*

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
                typeToken = TypeToken.getParameterized(Rest.LazyList::class.java, Media::class.java).type,
            )
            downloader.download(singleItemList.items.first(), idealSize, link)
        } else
            System.err.println("Shall we re-implement PageConfig?")
    }

    /** If a user doesn't exist, HTTP error code 404 will be thrown! */
    fun userInfo(userId: String): User =
        api.call<Rest.UserInfo>(Api.Endpoint.USER_INFO.url.format(userId), Rest.UserInfo::class).user

    /** If a user doesn't exist, HTTP error code 404 will be thrown! */
    fun profileInfo(userName: String): User =
        api.call<GraphQl>(Api.Endpoint.PROFILE_INFO.url.format(userName), GraphQl::class).data
            ?.let { it.user!! }
            ?: throw Api.FailureException(-3)

    /** Likes a post/reel or likes/unlikes a daily/highlighted story via the new GraphQl API. */
    fun likeMedia(med: Media, graphQlQuery: GraphQlQuery) {
        val unlike = graphQlQuery == GraphQlQuery.UNLIKE_STORY
        if (cancelLiking(med, unlike)) return
        val pk = med.pk ?: med.id.substringBefore("_")
        val gql = api.call<GraphQl>(
            Api.Endpoint.QUERY.url, GraphQl::class, true, graphQlQuery.body(pk)
        )
        likeMessage(med, unlike, gql.data == null)
    }

    /** Likes a post/reel via the classic REST API. */
    fun likePost(med: Media, unlike: Boolean = false) {
        if (cancelLiking(med, unlike)) return
        val rest = api.call<Rest.QuickResponse>(
            Api.Endpoint.UNLIKE_POST.url.format(), Rest.QuickResponse::class, true
        )
        likeMessage(med, unlike, rest.status == Utils.REST_STATUS_OK)
    }

    private fun cancelLiking(med: Media, unlike: Boolean): Boolean {
        if (!unlike && med.has_liked == true) {
            println("Already liked ${med.link()}")
            return true; }
        if (unlike && med.has_liked == false) {
            println("Already unliked ${med.link()}")
            return true; }
        return false
    }

    private fun likeMessage(med: Media, unlike: Boolean, success: Boolean) {
        if (success)
            println("Successfully ${if (unlike) "un" else ""}liked ${med.link()}")
        else
            System.err.println("Could not ${if (unlike) "un" else ""}like ${med.link()}")
    }
}
