package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.json.Api
import ir.mahdiparastesh.instatools.json.Api.Endpoint
import ir.mahdiparastesh.instatools.json.Rest

suspend fun main() { // args: Array<String>
    val api = Api()
    api.request<Rest.UserInfo>(Endpoint.INFO.url.format("8337021434"), Rest.UserInfo::class) { info ->
        println(info.user.visName())
    }
    api.client.close()
}
