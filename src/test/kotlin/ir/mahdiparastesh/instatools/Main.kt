package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl

fun main() {
    val api = Api()
    api.call<GraphQl>(
        Api.Endpoint.PROFILE_INFO.url.format("fsdfjksdbfdkjsbkajfkajfbadkbak"), GraphQl::class
    )
}
