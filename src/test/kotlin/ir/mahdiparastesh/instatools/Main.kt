package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.GraphQlQuery

fun main() {
    val api = Api()
    api.loadCookies()

    val res = api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        GraphQlQuery.LIKE_POST.body("3556229450513837363")
    )
    if (res.data == null) throw InvalidCommandException("Could not like!")
    else println("Liked!")
}
