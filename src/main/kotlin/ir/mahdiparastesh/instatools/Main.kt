package ir.mahdiparastesh.instatools

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun main(args: Array<String>) {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("https://ktor.io/")
    println(response.status)
    client.close()
}
