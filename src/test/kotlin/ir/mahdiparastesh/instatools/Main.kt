package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import org.apache.http.HttpHost
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

suspend fun main() {
    val cookies = ""//Api().cookies
    val client: CloseableHttpClient = HttpClients.custom()
        .setProxy(HttpHost("127.0.0.1", 8580, "http"))
        .build()
    val req = HttpGet(Api.Endpoint.USER_INFO.url.format("8337021434")).apply {
        addHeader("x-asbd-id", "129477")
        if (cookies.contains("csrftoken=")) addHeader(
            "x-csrftoken",
            cookies.substringAfter("csrftoken=").substringBefore(";")
        )
        addHeader("x-ig-app-id", "936619743392459")
        addHeader("cookie", cookies)
    }
    client.execute(req) { response -> // blocking
        println(response.statusLine.statusCode)
        println(EntityUtils.toString(response.entity))
    }
}

