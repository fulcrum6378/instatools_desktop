package ir.mahdiparastesh.instatools.api

import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import ir.mahdiparastesh.instatools.util.Utils
import org.apache.http.client.methods.HttpRequestBase
import java.io.File
import java.io.FileInputStream
import java.net.InetAddress
import kotlin.reflect.KClass

class Api {
    val client = HttpClient(CIO) {
        followRedirects = false
        if (InetAddress.getLocalHost().hostName in arrayOf("CHIMAERA", "ANGELDUST"))
            engine { proxy = ProxyBuilder.http("http://127.0.0.1:8580/") }
    }
    var cookies = ""

    fun loadCookies(path: String = "cookies.txt"): Boolean {
        val f = File(path)
        if (!f.exists()) return false
        cookies = FileInputStream(f).use { String(it.readBytes()) }
        return true
    }

    @Suppress("UastIncorrectHttpHeaderInspection")
    suspend fun <JSON> call(
        url: String,
        clazz: KClass<*>,
        httpMethod: HttpMethod = HttpMethod.Get,
        body: String? = null,
        typeToken: java.lang.reflect.Type? = null,
        onSuccess: suspend (json: JSON) -> Unit
    ) {
        val response: HttpResponse = client.request(url) {
            method = httpMethod
            headers {
                append("x-asbd-id", "129477")
                if (cookies.contains("csrftoken=")) append(
                    "x-csrftoken",
                    cookies.substringAfter("csrftoken=").substringBefore(";")
                )
                append("x-ig-app-id", "936619743392459")
                append("cookie", cookies)
            }
            if (body != null) setBody(body)
            timeout { requestTimeoutMillis = 8000L }
        }
        val text = response.bodyAsText()
        if (System.getenv("debug") == "1")
            println(text)
        if (response.status == HttpStatusCode.OK)
            onSuccess(Gson().fromJson(text, typeToken ?: clazz.java) as JSON)
        else
            error(response.status.value)
    }

    suspend fun page(
        url: String,
        onSuccess: suspend (html: String) -> Unit
    ) {
        val response: HttpResponse = client.get(url) {
            headers {
                append("accept", "text/html")
                append("cookie", cookies)
            }
            timeout { requestTimeoutMillis = 10000L }
        }
        if (response.status == HttpStatusCode.OK)
            onSuccess(response.bodyAsText())
        else
            error(response.status.value)
    }

    private fun error(status: Int) {
        when (status) {
            302 -> System.err.println("Found redirection!")
            429 -> System.err.println("Too many requests!")
            else -> System.err.println("HTTP error code $status!")
        }
    }

    enum class Endpoint(val url: String) {
        // Profiles
        PROFILE("https://www.instagram.com/api/v1/users/web_profile_info/?username=%s"),
        USER_INFO("https://www.instagram.com/api/v1/users/%s/info/"),
        SEARCH(
            "https://www.instagram.com/api/v1/web/search/topsearch/?context=blended&query=%s" +
                    "&include_reel=false&search_surface=web_top_search"
        ), // &rank_token=0.9366187585704904

        // Posts & Stories
        MEDIA_INFO("https://www.instagram.com/api/v1/media/%s/info/"),
        POSTS(
            "https://www.instagram.com/graphql/query/?query_hash=${Utils.GRAPHQL_POST_HASH}" +
                    "&variables={\"id\":\"%1\$s\",\"first\":12,\"after\":\"%2\$s\"}"
        ),
        TAGGED("https://www.instagram.com/api/v1/usertags/%1\$s/feed/?count=12&max_id=%2\$s"),
        STORY("https://www.instagram.com/api/v1/feed/user/%s/story/"),
        HIGHLIGHTS("https://www.instagram.com/api/v1/highlights/%s/highlights_tray/"),
        REEL_ITEM("https://www.instagram.com/api/v1/feed/reels_media/?reel_ids=%s"),
        // StoryReel = "Full-Screen Video"; Story { reel, reel, ... }, Highlights { reel, reel, ... }
        // Adding "media_id=" parameter is of no use, the results are the same!!
        /*NEW_TAGGED( // Requires edges again
            "https://www.instagram.com/graphql/query/?query_hash=$taggedHash" +
                    "&variables={\"id\":\"%1\$s\",\"first\":12,\"after\":\"%2\$s\"}"
        ),*///const val taggedHash = "be13233562af2d229b008d2976b998b5"

        // Interactions (always use "?count=" for more accurate results)
        /*FOLLOWERS("https://www.instagram.com/api/v1/friendships/%1\$s/followers/?count=200&max_id=%2\$s"),
        FOLLOWING("https://www.instagram.com/api/v1/friendships/%1\$s/following/?count=200&max_id=%2\$s"),
        FRIENDSHIPS_MANY("https://www.instagram.com/api/v1/friendships/show_many/"),
        // method = POST, `user_ids=<ids separated by ",">`, expect Rest$Friendships
        FRIENDSHIP("https://www.instagram.com/api/v1/friendships/show/%s/"),*/ // GET

        FOLLOW("https://www.instagram.com/api/v1/friendships/create/%s/"),
        UNFOLLOW("https://www.instagram.com/api/v1/friendships/destroy/%s/"),
        MUTE("https://www.instagram.com/api/v1/friendships/mute_posts_or_story_from_follow/"),
        UNMUTE("https://www.instagram.com/api/v1/friendships/unmute_posts_or_story_from_follow/"),

        // method = POST, "target_posts_author_id=<USER_ID>" AND(using &)/OR "target_reel_author_id=<USER_ID>",
        // expect Rest$Friendships
        RESTRICT("https://www.instagram.com/api/v1/web/restrict_action/restrict/"),
        UNRESTRICT("https://www.instagram.com/api/v1/web/restrict_action/unrestrict/"),

        // method = POST, body = "target_user_id=<USER_ID>", expect "{"status":"ok"}"
        BLOCK("https://www.instagram.com/api/v1/web/friendships/%d/block/"),
        UNBLOCK("https://www.instagram.com/api/v1/web/friendships/%d/unblock/"),
        // method = POST, expect {"status":"ok"}

        // Saving
        SAVED("https://www.instagram.com/api/v1/feed/saved/posts/"),
        UNSAVE("https://www.instagram.com/api/v1/web/save/%s/unsave/"),
        SAVE("https://www.instagram.com/api/v1/web/save/%s/save/"),

        // Messaging
        INBOX("https://www.instagram.com/api/v1/direct_v2/inbox/?cursor=%s"),
        DIRECT("https://www.instagram.com/api/v1/direct_v2/threads/%1\$s/?cursor=%2\$s&limit=%3\$d"),/*
        // persistentBadging=true&folder=[0(PRIMARY)|1(GENERAL)]
        // Avoiding "limit" argument will default to 20, but can be more than that. */
        SEEN("https://www.instagram.com/api/v1/direct_v2/threads/%1\$s/items/%2\$s/seen/"),

        // Logging in/out
        SIGN_OUT("https://www.instagram.com/accounts/logout/ajax/"),// MEDIA_ITEM

        RAW_QUERY("https://www.instagram.com/graphql/query"),
    }
}
