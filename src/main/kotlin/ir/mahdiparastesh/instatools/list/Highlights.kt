package ir.mahdiparastesh.instatools.list

import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.Context.downloader
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.api.Story
import ir.mahdiparastesh.instatools.util.Lister
import ir.mahdiparastesh.instatools.util.Option
import ir.mahdiparastesh.instatools.util.Profile

class Highlights(override val p: Profile) : Lister<Media>(), Profile.Section {
    private val trays: HashMap<String, Story> = hashMapOf()
    private var currentTray: String? = null
    override val list: ArrayList<Media>
        get() = trays[currentTray!!]!!.items!! as ArrayList
    override val numberOfClauses: Int = 2

    override fun fetch() {
        p.requireUserId()
        val hls = api.call<GraphQl>(
            Api.Endpoint.QUERY.url, GraphQl::class, true,
            Api.GraphQlQuery.PROFILE_HIGHLIGHTS_TRAY.body(p.userId!!)
        ).data?.highlights?.edges
        if (hls == null) throw Api.FailureException(-3)

        if (hls.isEmpty()) println("This user has no highlighted stories.")
        else hls.forEachIndexed { _, tray ->
            val hlId = tray.node.highlightId()
            println(
                "$hlId:" +
                        (if (tray.node.title != null) " ${tray.node.title} -" else "") +
                        " ${tray.node.link()}" +
                        (if (tray.node.items != null) " (${tray.node.items!!.size} items)" else "")
            )
            if (hlId in trays && trays[hlId]!!.items == null)
                tray.node.items = trays[hlId]!!.items
            trays[hlId] = tray.node
        }
    }

    override fun fetch(reset: Boolean) {
        fetch()
    }

    override fun download(
        a: Array<String>, offsetOfClauses: Int, opt: HashMap<String, String?>?
    ) {
        currentTray =
            if (a[offsetOfClauses].startsWith("highlight:")) a[offsetOfClauses].substring(10)
            else a[offsetOfClauses]
        val ready = trays[currentTray]?.items
        if (ready == null) {
            val apiId = "\"highlight:${currentTray}\""
            val page = api.call<GraphQl>(
                Api.Endpoint.QUERY.url, GraphQl::class, true,
                Api.GraphQlQuery.HIGHLIGHTS.body(apiId, apiId)
            ).data?.xdt_api__v1__feed__reels_media__connection
                ?: throw Api.FailureException(-3)
            page.edges.forEach { tray ->
                tray.node.items = ArrayList(tray.node.items!!)
                trays[currentTray!!] = tray.node
            }
        }
        if (a.size == offsetOfClauses + 1)
            println("This tray contains ${trays[currentTray!!]!!.items!!.size} items.")
        else this[a[offsetOfClauses + 1]]?.forEach { med ->
            downloader.download(med, Option.quality(opt?.get(Option.QUALITY.key)), owner = p.userName)
        }
    }
}
