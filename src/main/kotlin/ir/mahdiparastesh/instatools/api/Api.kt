package ir.mahdiparastesh.instatools.api

import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.File
import java.io.FileInputStream
import kotlin.reflect.KClass

class Api {
    val client = HttpClient(CIO) {
        install(HttpCookies)
        engine {
            proxy = ProxyBuilder.http("http://127.0.0.1:8580/")
        }
    }
    private var cookies: String? = null  // FIXME SAVE COOKIES ON DESTROY
    private var cookiesInjectedOnce = false

    fun loadCookies(path: String = "cookies.txt"): Boolean {
        val f = File(path)
        if (!f.exists()) return false
        cookies = FileInputStream(f).use { String(it.readBytes()) }
        return true
    }

    private fun HttpMessageBuilder.injectCookies() {
        //if (cookiesInjectedOnce) return
        var kv: List<String>
        for (cookie in cookies!!.split("; ")) {
            kv = cookie.split("=")
            cookie(kv[0], kv[1], domain = IG_DOMAIN)
        }
        cookiesInjectedOnce = true
    }

    @Suppress("SpellCheckingInspection", "UastIncorrectHttpHeaderInspection")
    suspend fun <JSON> call(
        url: String,
        clazz: KClass<*>,
        httpMethod: HttpMethod = HttpMethod.Get,
        body: String? = null,
        typeToken: java.lang.reflect.Type? = null,
        onError: ((status: Int, body: String) -> Unit)? = null,
        onSuccess: suspend (json: JSON) -> Unit
    ) {
        val response: HttpResponse = client.request(url) {
            method = httpMethod
            headers {
                append("accept", "*/*")
                append("x-asbd-id", "129477")
                if (cookies!!.contains("csrftoken=")) append(
                    "x-csrftoken",
                    cookies!!.substringAfter("csrftoken=").substringBefore(";")
                )
                append("x-ig-app-id", "936619743392459")
            }
            injectCookies()
            if (body != null) setBody(body)
        }
        val text = response.bodyAsText()
        if (System.getenv("test") == "1")
            println(text)
        if (response.status == HttpStatusCode.OK)
            onSuccess(Gson().fromJson(text, typeToken ?: clazz.java) as JSON)
        else {
            if (onError != null) onError(response.status.value, text)
        }
    }

    /*fetch("https://www.instagram.com/lucy.ai23/",
    {
        "headers": {
        "accept-language": "en-GB,en;q=0.9,fa-IR;q=0.8,fa;q=0.7,es-US;q=0.6,es;q=0.5,ru-RU;q=0.4,ru;q=0.3,de-DE;q=0.2,de;q=0.1,cs-CZ;q=0.1,cs;q=0.1,en-US;q=0.1",
        "dpr": "1",
        "priority": "u=0, i",
        "sec-ch-prefers-color-scheme": "light",
        "sec-ch-ua": "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"",
        "sec-ch-ua-full-version-list": "\"Google Chrome\";v=\"131.0.6778.140\", \"Chromium\";v=\"131.0.6778.140\", \"Not_A Brand\";v=\"24.0.0.0\"",
        "sec-ch-ua-mobile": "?0",
        "sec-ch-ua-model": "\"\"",
        "sec-ch-ua-platform": "\"Windows\"",
        "sec-ch-ua-platform-version": "\"10.0.0\"",
        "sec-fetch-dest": "document",
        "sec-fetch-mode": "navigate",
        "sec-fetch-site": "none",
        "sec-fetch-user": "?1",
        "upgrade-insecure-requests": "1",
        "viewport-width": "1366",
    },
        "referrerPolicy": "strict-origin-when-cross-origin",
        "body": null,
        "method": "GET"
    });*/

    suspend fun page(
        url: String,
        onError: (status: Int) -> Unit,
        onSuccess: suspend (html: String) -> Unit
    ) {
        val response: HttpResponse = client.get(url) {
            headers {
                append(
                    "accept", "text/html,application/xhtml+xml,application/xml"
                )
            }
            injectCookies()
        }
        if (response.status == HttpStatusCode.OK)
            onSuccess(response.bodyAsText())
        else
            onError(response.status.value)
    }

    companion object {
        const val IG_DOMAIN = "https://www.instagram.com/"
        const val POST_HASH = "8c2a529969ee035a5063f2fc8602a0fd"

        /** Converts a seconds timestamp to a milliseconds one. */
        fun Double.xFromSeconds() = toLong() * 1000L
    }

    @Suppress("unused")
    enum class Endpoint(val url: String) {
        // Profiles
        PROFILE("https://www.instagram.com/api/v1/users/web_profile_info/?username=%s"),
        INFO("https://www.instagram.com/api/v1/users/%s/info/"),
        SEARCH(
            "https://www.instagram.com/api/v1/web/search/topsearch/?context=blended&query=%s" +
                    "&include_reel=false&search_surface=web_top_search"
        ), // &rank_token=0.9366187585704904

        // Posts & Stories
        MEDIA_ITEM("https://www.instagram.com/api/v1/media/%s/info/"),
        POSTS(
            "https://www.instagram.com/graphql/query/?query_hash=$POST_HASH" +
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
        FOLLOWERS("https://www.instagram.com/api/v1/friendships/%1\$s/followers/?count=200&max_id=%2\$s"),
        FOLLOWING("https://www.instagram.com/api/v1/friendships/%1\$s/following/?count=200&max_id=%2\$s"),
        FRIENDSHIPS_MANY("https://www.instagram.com/api/v1/friendships/show_many/"), /*
        // method = POST, `user_ids=<ids separated by ",">`, expect Rest$Friendships *//*
        FRIENDSHIP("https://www.instagram.com/api/v1/friendships/show/%s/"), // GET */

        //FOLLOW("https://www.instagram.com/api/v1/friendships/create/%s/"),
        UNFOLLOW("https://www.instagram.com/api/v1/friendships/destroy/%s/"),
        /*MUTE("https://www.instagram.com/api/v1/friendships/mute_posts_or_story_from_follow/"),
        UNMUTE("https://www.instagram.com/api/v1/friendships/unmute_posts_or_story_from_follow/"),
        // method = POST, "target_posts_author_id=<USER_ID>" AND(using &)/OR "target_reel_author_id=<USER_ID>",
        // expect Rest$Friendships*/
        /*RESTRICT("https://www.instagram.com/api/v1/web/restrict_action/restrict/"),
        UNRESTRICT("https://www.instagram.com/api/v1/web/restrict_action/unrestrict/"),
        // method = POST, body = "target_user_id=<USER_ID>", expect "{"status":"ok"}" */
        /*BLOCK("https://www.instagram.com/api/v1/web/friendships/%d/block/"),
        UNBLOCK("https://www.instagram.com/api/v1/web/friendships/%d/unblock/"),
        // method = POST, expect "{"status":"ok"}" */

        // Saving
        SAVED("https://www.instagram.com/api/v1/feed/saved/posts/"),
        UNSAVE("https://www.instagram.com/web/save/%s/unsave/"),
        //SAVE("https://www.instagram.com/web/save/%s/save/"),
        // The fucking web API used /web/save for fulcrum6378 and /graphql/query for instatools.apk !?!

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

    public class CustomCookiesStorage : CookiesStorage {
        override suspend fun get(requestUrl: Url): List<Cookie> {
            TODO("Not yet implemented")
        }

        override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {

        }

        override fun close() {

        }
    }
}
