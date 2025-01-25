package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl

fun main() {
    val api = Api()
    val size = api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        Api.GraphQlQuery.PROFILE_POSTS.body("fulcrum6378", "35", "null")
    ).data?.xdt_api__v1__feed__user_timeline_graphql_connection?.edges?.size
    println("Got $size items!") // 33
}
