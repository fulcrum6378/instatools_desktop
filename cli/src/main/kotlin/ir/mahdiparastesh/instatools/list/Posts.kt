package ir.mahdiparastesh.instatools.list

import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.Context.downloader
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.GraphQlQuery
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.util.Lister.LazyLister
import ir.mahdiparastesh.instatools.util.Option
import ir.mahdiparastesh.instatools.util.Profile
import ir.mahdiparastesh.instatools.util.SimpleActions

class Posts(override val p: Profile) : LazyLister<Media>(), Profile.Section {
    override val numberOfClauses: Int = 1

    override fun fetch() {
        super.fetch()
        val page = api.call<GraphQl>(
            Api.Endpoint.QUERY.url, GraphQl::class, true,
            GraphQlQuery.PROFILE_POSTS.body(p.userName, "33", cursor.toString())
        ).data?.xdt_api__v1__feed__user_timeline_graphql_connection
        if (page == null) throw Api.FailureException(-3)
        if (p.userId == null && page.edges.isNotEmpty())
            p.userId = page.edges.first().node.user?.pk

        var caption: String
        for (e in page.edges) {
            caption = e.node.caption?.text?.replace("\n", " ")?.let { ": $it" } ?: ""
            println("$index. ${e.node.link()}$caption")
            add(e.node)
        }
        if (page.page_info.has_next_page) {
            cursor = page.edges.last().node.id
            println("Enter `p ${p.userName}` again or just `p` to load more posts from their profile...")
        } else endOfList()
    }

    override fun fetch(reset: Boolean) {
        fetchSome(reset)
    }

    override fun download(
        a: Array<String>, offsetOfClauses: Int, opt: HashMap<String, String?>?
    ) {
        this[a[offsetOfClauses]].forEach { med ->
            downloader.download(med, Option.quality(opt?.get(Option.QUALITY.key)))
            if (opt?.contains(Option.LIKE.key) == true)
                SimpleActions.likeMedia(med, GraphQlQuery.LIKE_POST)
        }
    }
}
