package ir.mahdiparastesh.instatools.list

import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.util.LazyLister
import ir.mahdiparastesh.instatools.util.Profile

class Tagged(private val p: Profile) : LazyLister<Media>() {

    override fun fetch(reset: Boolean) {
        super.fetch(reset)
        p.requireUserId()
        val page = api.call<GraphQl>(
            Api.Endpoint.QUERY.url, GraphQl::class, true,
            if (cursor == null && !reset)
                Api.GraphQlQuery.PROFILE_TAGGED.body(p.userId!!, "36")
            else
                Api.GraphQlQuery.PROFILE_TAGGED_CURSORED.body(p.userId!!, "36", cursor!!)
        ).data?.xdt_api__v1__usertags__user_id__feed_connection
        if (page == null) throw Api.FailureException(-2)

        for (e in page.edges) {
            println(
                "$index. ${e.node.link()} - @${e.node.owner().username} : " +
                        "${e.node.caption?.text?.replace("\n", " ")}"
            )
            add(e.node)
        }
        if (page.page_info.has_next_page) {
            cursor = page.edges.last().node.pk
            println("Enter `t ${p.userName}` again or just `t` to load more tagged posts from their profile...")
        } else endOfList()
    }
}
