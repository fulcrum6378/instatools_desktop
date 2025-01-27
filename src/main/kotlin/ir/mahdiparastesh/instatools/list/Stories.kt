package ir.mahdiparastesh.instatools.list

import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.util.Lister.OneTimeLister
import ir.mahdiparastesh.instatools.util.Profile

class Stories(private val p: Profile) : OneTimeLister<Media>() {

    override fun fetch() {
        p.requireUserId()
        val reels = api.call<GraphQl>(
            Api.Endpoint.QUERY.url, GraphQl::class, true, Api.GraphQlQuery.STORY.body(p.userId!!)
        ).data?.xdt_api__v1__feed__reels_media?.reels_media
        if (reels == null) throw Api.FailureException(-3)

        val media = reels.firstOrNull()?.items
        if (media.isNullOrEmpty())
            println("This user has no stories.")
        else media.forEachIndexed { index, r ->
            println("${index + 1}. " + r.link(p.userName))
            add(r)
        }
    }
}
