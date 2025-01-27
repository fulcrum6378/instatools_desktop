package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl

fun main() {
    val api = Api()
    api.loadCookies()

    val hls = api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        Api.GraphQlQuery.PROFILE_HIGHLIGHTS_TRAY.body("2003616263")
    ).data?.highlights?.edges
    if (hls == null) throw Api.FailureException(-3)

    if (hls.isEmpty()) println("No highlighted stories.")
    else hls.forEachIndexed { index, tray ->
        println(
            "${index + 1}. ${tray.node.link()} -" +
                    (if (tray.node.title != null) " ${tray.node.title}" else "") +
                    (if (tray.node.items != null) " (${tray.node.items!!.size} items)" else "")
        )
    }

    /*hls.map { "\"${it.node.id}\"" }
    api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        Api.GraphQlQuery.HIGHLIGHTS.body(hls.joinToString(","), hls[0])
    ).data?.xdt_api__v1__feed__reels_media__connection?.also { page ->
        page.edges.forEach { tray ->
            tray.node.items!!.forEachIndexed { index, story ->
                println("${tray.node.id} : ${index + 1}. " + story.link(""))
            }
        }
    } ?: throw Api.FailureException(-3)*/
}
