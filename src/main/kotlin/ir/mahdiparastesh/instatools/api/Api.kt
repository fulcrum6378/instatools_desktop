package ir.mahdiparastesh.instatools.api

import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.reflect.KClass

class Api {
    val client = HttpClient(CIO) {
        /*install(HttpCookies) {
            storage = CustomCookiesStorage()
        }*/
        engine {
            proxy = ProxyBuilder.http("http://127.0.0.1:8580/")
        }
    }
    private var fCookies: File? = null
    private var cookies = hashMapOf<String, String>()

    fun loadCookies(path: String = "cookies.txt"): Boolean {
        fCookies = File(path)
        if (!fCookies!!.exists()) return false
        var kv: List<String>
        FileInputStream(fCookies!!).use { String(it.readBytes()) }.split("; ").forEach {
            kv = it.split("=")
            if (kv.size != 2) return@forEach
            cookies[kv[0]] = kv[1]
        }
        return true
    }

    @Suppress("UastIncorrectHttpHeaderInspection")
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
                //append("accept", "*/*")
                /*append("accept-language", "en-GB,en;q=0.9,fa-IR;q=0.8,fa;q=0.7,es-US;q=0.6,es;q=0.5,ru-RU;q=0.4,ru;q=0.3,de-DE;q=0.2,de;q=0.1,cs-CZ;q=0.1,cs;q=0.1,en-US;q=0.1")
                append("sec-ch-prefers-color-scheme", "light")
                append("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"")
                append("sec-ch-ua-full-version-list", "\"Google Chrome\";v=\"131.0.6778.140\", \"Chromium\";v=\"131.0.6778.140\", \"Not_A Brand\";v=\"24.0.0.0\"")
                append("sec-ch-ua-mobile", "?0")
                append("sec-ch-ua-model", "\"\"")
                append("sec-ch-ua-platform", "\"Windows\"")
                append("sec-fetch-dest", "empty")
                append("sec-fetch-mode", "cors")
                append("sec-fetch-site", "same-origin")*/
                append("x-asbd-id", "129477")
                if (COOKIE_CSRF_TOKEN in cookies)
                    append("x-csrftoken", cookies[COOKIE_CSRF_TOKEN]!!)
                append("x-ig-app-id", "936619743392459")
                /*append("x-ig-www-claim", "0")
                append("x-requested-with", "XMLHttpRequest")
                append("x-web-session-id", "8yez8t:v0b03t:60m5bn")*/
                append("cookie", "ps_l=1; ps_n=1; mid=Zwkt3wALAAGiYlxgtUWozgf2unFE; datr=3i0JZ0Yom4G5r0sOVfvL8lFl; ig_did=42D71865-09BA-4449-8A17-7E98FF37A876; fbm_124024574287414=base_domain=.instagram.com; fbsr_124024574287414=IGPINZKyJzrNtoGUVjzM_c_CVvzrQz8-K93bBkGCWH8.eyJ1c2VyX2lkIjoiMTAwMDA4MDA0ODE0NzI1IiwiY29kZSI6IkFRQlNKRkQ5REE1WXpDNGoxYjVqQWFQeXdMd1JFTnFJUXdZNUduTG1qV2U5YzdPYUhWXzBmUEFVc1g4Y3FVSTZFZ0wwWTlWVEt3OVVTZmlyN1NKbFNkMWdUUXZfLTV0YzNhdjR3ZW15SWFfNnl2ZXZpb00tY1hZSTIyOUtzTFlXc3k0RUpYWDhtUnY0VEVNbjhRRFJYNURXdlMzLWlQelFFcG9xajVweUtFU3dPdnhib3VLWVdIMkhDajllNmtsWk1NSmFydUlzUDlLRGE0cHNEdGdZVlhvZl81QXRwYnZuVWNlTWtYa0ZySzhTaUdKY2FUZnYxQWtwUDFhN2tISHZ5MTN1SUJKMjc3NE9yQ0stU1lLa1k5ckc3SzFid3hxUEJnVEw0V0kzQ2hhbVZOSnhscUNPcVpORW40UVNrT185QzB6NE5VMmhNQUZ3T0NHVFBFaDZ0TjV4Iiwib2F1dGhfdG9rZW4iOiJFQUFCd3pMaXhuallCTzdxSGFMb1VoWDhLWkJmcHNKQ1RxbHBPSzhBUnFrZVRqWkF0VlpBbktXamREcmlWOUFTd1I4b1N3NHUzYlpBUHFzZUNySzRYYTd4enE1dlNFTzNBRWJaQW5MTmtFMmFWMENLeEx6bUZoSTM5WkFTZ1NxVG5tcGtlY3BPV2pHbDBoQkpUeHRlRVpCcmNPem5LMVd5dlJaQUpGOWI5bmNuYUtSbkJvNVdzVFpBdWZGMENzdXBiSkZGY250Z255MFpBdnZ2WkJsTmU3ZFpCb3BrWkQiLCJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTczMjIwODc3OX0; csrftoken=9CZmZa1yWdODfz7L08cnaJerQJoH6UX2; ds_user_id=8337021434; ig_direct_region_hint=\"FRC\\0548337021434\\0541768598626:01f7bf573fba19360c60be85233440f5d13483b6da6018fed9b2addf37eaf33e8d9263ea\"; sessionid=8337021434%3A16QWbheL5V0m9T%3A22%3AAYe3xnwQDWirm5AfHj8vTrTz3dQZN-g4Kp9vZpqTRBo; wd=1366x389; rur=\"NHA\\0548337021434\\0541768726176:01f77e572643858ef666e1dd0b3487ace9fcd4b9f3a465a71e73e2c396a88b77dc8eb84a\"")
                /*append("Referer", "https://www.instagram.com/lucy.ai23/p/DEzbnrfoCdD/")
                append("Referrer-Policy", "strict-origin-when-cross-origin")*/
            }
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

    /*fetch("https://www.instagram.com/api/v1/media/3545298805687592771/info/", {
        "headers": {
            "accept": "*//*",
            "accept-language": "en-GB,en;q=0.9,fa-IR;q=0.8,fa;q=0.7,es-US;q=0.6,es;q=0.5,ru-RU;q=0.4,ru;q=0.3,de-DE;q=0.2,de;q=0.1,cs-CZ;q=0.1,cs;q=0.1,en-US;q=0.1",
            "priority": "u=1, i",
            "sec-ch-prefers-color-scheme": "light",
            "sec-ch-ua": "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"",
            "sec-ch-ua-full-version-list": "\"Google Chrome\";v=\"131.0.6778.140\", \"Chromium\";v=\"131.0.6778.140\", \"Not_A Brand\";v=\"24.0.0.0\"",
            "sec-ch-ua-mobile": "?0",
            "sec-ch-ua-model": "\"\"",
            "sec-ch-ua-platform": "\"Windows\"",
            "sec-ch-ua-platform-version": "\"10.0.0\"",
            "sec-fetch-dest": "empty",
            "sec-fetch-mode": "cors",
            "sec-fetch-site": "same-origin",
            "x-asbd-id": "129477",
            "x-csrftoken": "9CZmZa1yWdODfz7L08cnaJerQJoH6UX2",
            "x-ig-app-id": "936619743392459",
            "x-ig-www-claim": "0",
            "x-requested-with": "XMLHttpRequest",
            "x-web-session-id": "8yez8t:v0b03t:60m5bn",
            "cookie": "ps_l=1; ps_n=1; mid=Zwkt3wALAAGiYlxgtUWozgf2unFE; datr=3i0JZ0Yom4G5r0sOVfvL8lFl; ig_did=42D71865-09BA-4449-8A17-7E98FF37A876; fbm_124024574287414=base_domain=.instagram.com; fbsr_124024574287414=IGPINZKyJzrNtoGUVjzM_c_CVvzrQz8-K93bBkGCWH8.eyJ1c2VyX2lkIjoiMTAwMDA4MDA0ODE0NzI1IiwiY29kZSI6IkFRQlNKRkQ5REE1WXpDNGoxYjVqQWFQeXdMd1JFTnFJUXdZNUduTG1qV2U5YzdPYUhWXzBmUEFVc1g4Y3FVSTZFZ0wwWTlWVEt3OVVTZmlyN1NKbFNkMWdUUXZfLTV0YzNhdjR3ZW15SWFfNnl2ZXZpb00tY1hZSTIyOUtzTFlXc3k0RUpYWDhtUnY0VEVNbjhRRFJYNURXdlMzLWlQelFFcG9xajVweUtFU3dPdnhib3VLWVdIMkhDajllNmtsWk1NSmFydUlzUDlLRGE0cHNEdGdZVlhvZl81QXRwYnZuVWNlTWtYa0ZySzhTaUdKY2FUZnYxQWtwUDFhN2tISHZ5MTN1SUJKMjc3NE9yQ0stU1lLa1k5ckc3SzFid3hxUEJnVEw0V0kzQ2hhbVZOSnhscUNPcVpORW40UVNrT185QzB6NE5VMmhNQUZ3T0NHVFBFaDZ0TjV4Iiwib2F1dGhfdG9rZW4iOiJFQUFCd3pMaXhuallCTzdxSGFMb1VoWDhLWkJmcHNKQ1RxbHBPSzhBUnFrZVRqWkF0VlpBbktXamREcmlWOUFTd1I4b1N3NHUzYlpBUHFzZUNySzRYYTd4enE1dlNFTzNBRWJaQW5MTmtFMmFWMENLeEx6bUZoSTM5WkFTZ1NxVG5tcGtlY3BPV2pHbDBoQkpUeHRlRVpCcmNPem5LMVd5dlJaQUpGOWI5bmNuYUtSbkJvNVdzVFpBdWZGMENzdXBiSkZGY250Z255MFpBdnZ2WkJsTmU3ZFpCb3BrWkQiLCJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTczMjIwODc3OX0; csrftoken=9CZmZa1yWdODfz7L08cnaJerQJoH6UX2; ds_user_id=8337021434; ig_direct_region_hint=\"FRC\\0548337021434\\0541768598626:01f7bf573fba19360c60be85233440f5d13483b6da6018fed9b2addf37eaf33e8d9263ea\"; sessionid=8337021434%3A16QWbheL5V0m9T%3A22%3AAYe3xnwQDWirm5AfHj8vTrTz3dQZN-g4Kp9vZpqTRBo; wd=1366x389; rur=\"NHA\\0548337021434\\0541768726176:01f77e572643858ef666e1dd0b3487ace9fcd4b9f3a465a71e73e2c396a88b77dc8eb84a\"",
            "Referer": "https://www.instagram.com/lucy.ai23/p/DEzbnrfoCdD/",
            "Referrer-Policy": "strict-origin-when-cross-origin"
        },
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
                append("accept", "text/html")
            }
        }
        if (response.status == HttpStatusCode.OK)
            onSuccess(response.bodyAsText())
        else
            onError(response.status.value)
    }

    companion object {
        const val IG_DOMAIN = "https://www.instagram.com/"
        const val POST_HASH = "8c2a529969ee035a5063f2fc8602a0fd"
        const val COOKIE_CSRF_TOKEN = "csrftoken"

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
        MEDIA_INFO("https://www.instagram.com/api/v1/media/%s/info/"),
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

    inner class CustomCookiesStorage : CookiesStorage {
        override suspend fun get(requestUrl: Url): List<Cookie> =
            arrayListOf<Cookie>().apply {
                for ((k, v) in cookies) add(Cookie(k, v, domain = IG_DOMAIN))
            }

        @Suppress("SpellCheckingInspection")
        override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
            when (cookie.name) {
                "sessionid" -> {}  // IG will try to invalidate the session!
                else -> cookies[cookie.name] = cookie.value
            }
        }

        override fun close() {
            if (fCookies == null) return
            FileOutputStream(fCookies!!).use {
                val sb = StringBuilder()
                for ((k, v) in cookies) sb.append(k).append("=").append(v).append("; ")
                sb.delete(sb.length - 2, sb.length)
                it.write(sb.toString().encodeToByteArray())
            }
        }
    }
}
