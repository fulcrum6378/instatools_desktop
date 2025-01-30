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

    /** Likes a post. */
    fun likePost(med: Media) {
        if (med.has_liked == true) {
            println("Already liked ${med.link()}")
            return; }
        val pk = med.pk ?: med.id.substringBefore("_")
        val gql = api.call<GraphQl>(
            Api.Endpoint.QUERY.url, GraphQl::class, true, GraphQlQuery.LIKE_POST.body(pk)
        )
        if (gql.data == null)
            System.err.println("Could not like ${med.link()}")
        else
            println("Successfully liked ${med.link()}")
    }
}
