package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import java.net.URLEncoder

fun main() {
    val api = Api()
    val size = api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        Api.GraphQlQuery.PROFILE_TAGGED.body("1488426694", "12")
    ).data?.xdt_api__v1__usertags__user_id__feed_connection?.edges?.size
    println("Got $size items!") // 33
}
