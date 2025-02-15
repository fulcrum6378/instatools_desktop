package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.GraphQlQuery
import java.io.File
import java.io.FileInputStream
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URI
import javax.net.ssl.HttpsURLConnection

fun main() {
    /*val api = Api()
    api.loadCookies()

    val res = api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        GraphQlQuery.UNLIKE_STORY.body("3560051330863552587")
    )
    if (res.data == null) throw InvalidCommandException("Could not like!")
    else println("Liked!")*/

    val connection = (URI(
        "https://www.instagram.com/api/v1/feed/saved/posts/"
    ).toURL().openConnection(
        Proxy(Proxy.Type.HTTP, InetSocketAddress("127.0.0.1", 8580))
    ) as HttpsURLConnection).apply {
        requestMethod = "GET"

        val cookies = FileInputStream(File("cookies.txt")).use { String(it.readBytes()) }
        setRequestProperty("x-asbd-id", "129477")
        if (cookies.contains("csrftoken=")) setRequestProperty(
            "x-csrftoken",
            cookies.substringAfter("csrftoken=").substringBefore(";")
        )
        setRequestProperty("x-ig-app-id", "936619743392459")
        setRequestProperty("cookie", cookies)
    }
    println(connection.inputStream.bufferedReader().readText())
    //FileOutputStream(File("out.json")).use { it.write() }
}
