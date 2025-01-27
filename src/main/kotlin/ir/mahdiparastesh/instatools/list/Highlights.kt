package ir.mahdiparastesh.instatools.list

import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.Story
import ir.mahdiparastesh.instatools.util.Lister.OneTimeLister
import ir.mahdiparastesh.instatools.util.Profile

class Highlights(override val p: Profile) : OneTimeLister<Story>(), Profile.Section {
    override val numberOfClauses: Int = 2

    override fun fetch() {
        p.requireUserId()
        val hls = api.call<GraphQl>(
            Api.Endpoint.QUERY.url, GraphQl::class, true,
            Api.GraphQlQuery.PROFILE_HIGHLIGHTS_TRAY.body(p.userId!!)
        ).data?.highlights?.edges
        if (hls == null) throw Api.FailureException(-3)

        if (hls.isEmpty()) println("This user has no highlighted stories.")
        else hls.forEachIndexed { i, tray ->
            println(
                "${i + 1}. ${tray.node.link()} -" +
                        (if (tray.node.title != null) " ${tray.node.title}" else "") +
                        (if (tray.node.items != null) " (${tray.node.items!!.size} items)" else "")
            )
            add(tray.node)
        }
    }

    override fun fetch(reset: Boolean) {
        fetchAll()
    }

    override fun download(
        a: Array<String>, offsetSinceItemNumbers: Int, opt: HashMap<String, String?>?
    ) {
        /*TODO this[a[offsetSinceItemNumbers]]?.forEach { med ->
            downloader.download(med, Option.quality(opt?.get(Option.QUALITY.key)), owner = p.userName)
        }*/
    }
}
