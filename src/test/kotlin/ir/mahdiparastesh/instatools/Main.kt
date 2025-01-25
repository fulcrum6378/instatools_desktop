package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import java.net.URLEncoder

fun main() {
    val api = Api()
    val size = api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        "doc_id=8786107121469577&variables=" +
                URLEncoder.encode(
                    "{" +
                            //"\"after\":\"null\"," + // it worked when I turned this off!!
                            "\"first\":12," +
                            "\"count\":12," +
                            "\"user_id\":\"1488426694\"" +
                            "}",
                    "utf-8"
                )
        //Api.GraphQlQuery.PROFILE_TAGGED_TAB_CONTENT.body("1488426694", "12", "null")
    ).data?.xdt_api__v1__usertags__user_id__feed_connection?.edges?.size
    println("Got $size items!") // 33
}
