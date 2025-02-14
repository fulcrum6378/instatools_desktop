package ir.mahdiparastesh.instatools.api

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import ir.mahdiparastesh.instatools.util.Utils
import org.apache.http.ConnectionClosedException
import org.apache.http.HttpHost
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.InetAddress
import java.net.URI
import java.net.URISyntaxException
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KClass

class Api {
    var client: CloseableHttpClient = createClient()
    private var cookies = ""

    init {
        Logger.getLogger("org.apache.http.client").setLevel(
            if (System.getenv("debug") == "1") Level.WARNING else Level.OFF
        )
    }

    private fun createClient(
        timeout: Int = 10000, proxy: String? = null
    ): CloseableHttpClient = HttpClients.custom().apply {
        setDefaultRequestConfig(
            RequestConfig.custom().setConnectTimeout(timeout).build()
        )

        if (proxy != null) try {
            val uri = URI(proxy)
            setProxy(HttpHost(uri.host, uri.port, uri.scheme))
        } catch (_: URISyntaxException) {
            throw FailureException(-10)
        }
        // special settings for our own computers
        else if (InetAddress.getLocalHost().hostName in arrayOf("CHIMAERA", "ANGELDUST"))
            setProxy(HttpHost("127.0.0.1", 8580, "http"))
    }.build()

    fun changeClient(timeout: Int = 10000, proxy: String? = null) {
        client = createClient(timeout, proxy)
    }

    fun loadCookies(path: String = "cookies.txt"): Boolean {
        val f = File(path)
        if (!f.exists()) return false
        cookies = FileInputStream(f).use { String(it.readBytes()) }
        return true
    }

    fun <JSON> call(
        url: String,
        clazz: KClass<*>,
        isPost: Boolean = false,
        body: String? = null,
        generics: Array<KClass<*>>? = null
    ): JSON {
        val request = (if (isPost) HttpPost(url) else HttpGet(url)).apply {
            addHeader("x-asbd-id", "129477")
            if (cookies.contains("csrftoken=")) addHeader(
                "x-csrftoken",
                cookies.substringAfter("csrftoken=").substringBefore(";")
            )
            addHeader("x-ig-app-id", "936619743392459")
            addHeader("cookie", cookies)
            if (this is HttpPost && body != null) {
                addHeader("content-type", "application/x-www-form-urlencoded")
                entity = StringEntity(body, "UTF-8")
                if (System.getenv("debug") == "1")
                    println("Post Body: $body")
            }
        }
        val response = try {
            client.execute(request)
        } catch (e: IOException) {
            throw FailureException(-1)
        }

        val text = try {
            EntityUtils.toString(response.entity)
        } catch (_: ConnectionClosedException) {
            throw FailureException(-2)
        }
        if (System.getenv("debug") == "1") {
            println(text)
            //FileOutputStream(File("Downloads/1.json")).use { it.write(text.encodeToByteArray()) }
        }
        if (response.statusLine.statusCode == 200) return try {
            Gson().fromJson(
                text,
                if (generics != null)
                    TypeToken.getParameterized(clazz.java, *generics.map { it.java }.toTypedArray()).type
                else clazz.java
            ) as JSON
        } catch (_: JsonSyntaxException) {
            println(text)
            throw FailureException(-3)
        } else
            throw FailureException(response.statusLine.statusCode)
    }

    fun page(url: String): String {
        val request = HttpGet(url).apply {
            addHeader("accept", "text/html")
            addHeader("cookie", cookies)
        }
        val response = try {
            client.execute(request)
        } catch (e: IOException) {
            throw FailureException(-1)
        }

        if (response.statusLine.statusCode != 200)
            throw FailureException(response.statusLine.statusCode)
        else
            return EntityUtils.toString(response.entity)
    }

    fun close() {
        client.close()
    }

    @Suppress("unused")
    enum class Endpoint(val url: String) {
        QUERY("https://www.instagram.com/graphql/query"),

        // Saving
        SAVED("https://www.instagram.com/api/v1/feed/saved/posts/"),

        // Direct
        INBOX("https://www.instagram.com/api/v1/direct_v2/inbox/?cursor=%s"),
        DIRECT("https://www.instagram.com/api/v1/direct_v2/threads/%1\$s/?cursor=%2\$s&limit=%3\$d"),
        SEEN("https://www.instagram.com/api/v1/direct_v2/threads/%1\$s/items/%2\$s/seen/"),

        // Info
        PROFILE_INFO("https://www.instagram.com/api/v1/users/web_profile_info/?username=%s"),
        USER_INFO("https://www.instagram.com/api/v1/users/%s/info/"),
        MEDIA_INFO("https://www.instagram.com/api/v1/media/%s/info/"),

        // Friendships (always use "?count=" for more accurate results)
        FOLLOWERS("https://www.instagram.com/api/v1/friendships/%1\$s/followers/?count=200&max_id=%2\$s"),
        FOLLOWING("https://www.instagram.com/api/v1/friendships/%1\$s/following/?count=200&max_id=%2\$s"),
        FRIENDSHIPS_MANY("https://www.instagram.com/api/v1/friendships/show_many/"),
        FRIENDSHIP("https://www.instagram.com/api/v1/friendships/show/%s/"), // GET
        FOLLOW("https://www.instagram.com/api/v1/friendships/create/%s/"),
        UNFOLLOW("https://www.instagram.com/api/v1/friendships/destroy/%s/"),
        MUTE("https://www.instagram.com/api/v1/friendships/mute_posts_or_story_from_follow/"),
        UNMUTE("https://www.instagram.com/api/v1/friendships/unmute_posts_or_story_from_follow/"),
        RESTRICT("https://www.instagram.com/api/v1/web/restrict_action/restrict/"),
        UNRESTRICT("https://www.instagram.com/api/v1/web/restrict_action/unrestrict/"),
        BLOCK("https://www.instagram.com/api/v1/web/friendships/%d/block/"),
        UNBLOCK("https://www.instagram.com/api/v1/web/friendships/%d/unblock/"),

        // Posts
        SAVE("https://www.instagram.com/api/v1/web/save/%s/save/"),
        UNSAVE("https://www.instagram.com/api/v1/web/save/%s/unsave/"),
        LIKE_POST("https://www.instagram.com/api/v1/web/likes/%s/like/"),
        UNLIKE_POST("https://www.instagram.com/api/v1/web/likes/%s/unlike/"),

        // Logging in/out
        LOGOUT("https://www.instagram.com/accounts/logout/ajax/")
    }

    class FailureException(status: Int) : IllegalStateException(
        "API ERROR: " + when (status) {
            -1 -> "Couldn't connect to Instagram!"
            -2 -> "Connection was broken!"
            -3 -> "Invalid response from Instagram!"
            -10 -> "Please enter a valid URI like the example above."
            302 -> "Found redirection!"
            401 -> "You've been logged out!"
            404 -> "Not found!"
            429 -> "Too many requests!"
            else -> "HTTP error code $status!"
        }
    ), Utils.InstaToolsException
}
