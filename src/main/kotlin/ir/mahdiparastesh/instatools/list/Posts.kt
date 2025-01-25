package ir.mahdiparastesh.instatools.list

import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.util.LazyLister
import ir.mahdiparastesh.instatools.util.Profile

class Posts(override val username: String) : LazyLister<Media>(), Profile.Lister {

    override fun fetch(reset: Boolean) {
        super.fetch(reset)
        val page = api.call<GraphQl>(
            Api.Endpoint.QUERY.url, GraphQl::class, true, Api.GraphQlQuery.PROFILE_POSTS
                .body(username, "33", if (cursor != null && !reset) cursor!! else "null")
        ).data?.xdt_api__v1__feed__user_timeline_graphql_connection
        if (page == null) throw Api.FailureException(-2)
        for (e in page.edges) {
            println(
                "$index. ${e.node.link()} : ${e.node.caption?.text?.replace("\n", " ")}"
            )
            add(e.node)
        }
        if (page.page_info.has_next_page) {
            cursor = page.edges.last().node.id
            println("Enter `p $username` again to load more posts from their profile...")
        } else endOfList()
    }
}
