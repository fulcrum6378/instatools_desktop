package ir.mahdiparastesh.instatools.list

import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.util.LazyLister
import ir.mahdiparastesh.instatools.util.Profile

class Posts(private val p: Profile) : LazyLister<Media>() {

    override fun fetch(reset: Boolean) {
        super.fetch(reset)
        val page = api.call<GraphQl>(
            Api.Endpoint.QUERY.url, GraphQl::class, true, Api.GraphQlQuery.PROFILE_POSTS
                .body(p.userName, "33", if (cursor != null && !reset) cursor!! else "null")
        ).data?.xdt_api__v1__feed__user_timeline_graphql_connection
        if (page == null) throw Api.FailureException(-3)
        if (p.userId == null && page.edges.isNotEmpty())
            p.userId = page.edges.first().node.user?.pk

        for (e in page.edges) {
            println("$index. ${e.node.link()} : ${e.node.caption?.text?.replace("\n", " ")}")
            add(e.node)
        }
        if (page.page_info.has_next_page) {
            cursor = page.edges.last().node.id
            println("Enter `p ${p.userName}` again or just `p` to load more posts from their profile...")
        } else endOfList()
    }
}
