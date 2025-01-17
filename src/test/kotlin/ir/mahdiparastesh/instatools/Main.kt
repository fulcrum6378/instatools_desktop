package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.Rest

suspend fun main() {
    val api = Api()
    api.call<Rest.UserInfo>(
        Api.Endpoint.INFO.url.format("8337021434"), Rest.UserInfo::class
    ) { info ->
        println(info.user.username)
    }
}
