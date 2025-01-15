package ir.mahdiparastesh.instatools

import io.ktor.http.*
import ir.mahdiparastesh.instatools.data.Queued
import ir.mahdiparastesh.instatools.json.*

class Downloader(private val api: Api) {

    suspend fun handleLink(link: String) {
        api.page(link, { status, html ->
            /*if (status == 429) {
                if (Downloads.active.value == true)
                    Downloads.handler?.obtainMessage(Downloads.HANDLE_429)?.sendToTarget()
                else eventNotification(Notify.ID_QUEUER_429) {
                    setContentTitle(getString(R.string.downloads))
                    setStyle(
                        NotificationCompat.BigTextStyle().bigText(
                            getString(R.string.queuer429)
                        )
                    )
                    setContentIntent(
                        PendingIntent.getActivity(
                            c, 0, Intent(c, Downloads::class.java), ntfMutability()
                        )
                    )
                }
                finish(true)
            } else {
                Api.gotError(this@Queuer, handler, null, it, HANDLE_HTML_ERROR)
                if (status != 404
                ) it.networkResponse?.data?.let { ba -> String(ba) }?.also { html ->
                    if (it.networkResponse?.statusCode == 500 &&
                        html.contains(Login.LOGGED_OUT_MSG_500)
                    ) needAuthentication()
                    else if (System.getenv("test") == "1")
                        throw Exception(status.toString())
                } // if it.networkResponse == null, just Api.gotError; slow internet connection!
            }*/
        }) { html ->
            PageConfig.findFromHtml(html, false, {
                if (it is PageConfig.Companion.NeedAuth) needAuthentication()
                else if (System.getenv("test") == "1") throw it
            }) { cnfWrapper ->
                if (cur.link.contains("/p/") || cur.link.contains("/reel/")
                    || cur.link.contains("/tv/")
                ) {
                    val shortcode = when {
                        cur.link.contains("/p/") -> cur.link.substringAfter("/p/")
                        cur.link.contains("/reel/") -> cur.link.substringAfter("/reel/")
                        cur.link.contains("/tv/") -> cur.link.substringAfter("/tv/")
                        else -> throw Exception("IMPOSSIBLE")
                    }.substringBefore("/")

                    api.call<GraphQl>(
                        Api.Endpoint.RAW_QUERY.url, GraphQl::class, HttpMethod.Post,
                        Api.graphQlBody(cnfWrapper, shortcode)
                    ) { graphQl ->
                        val med =
                            graphQl.data?.xdt_api__v1__media__shortcode__web_info?.items?.firstOrNull()
                                ?: throw IllegalStateException("med == null")
                        var found = true
                        val addOns = arrayListOf<Queued>()
                        when {
                            med.carousel_media != null -> for (car in med.carousel_media!!)
                                if (cur.qud!!.url == null) cur.qud!!.apply {
                                    date = med.taken_at.xFromSeconds()
                                    userId = med.user.pk
                                    userName = med.user.username
                                    itemId = car.pk
                                    url = car.nearest(Versioned.BEST)
                                    thumb = med.thumb()
                                    mediaType = car.media_type.toInt().toByte()
                                    dur = car.video_duration?.toLong()
                                    caption = med.caption?.text
                                } else addOns.add(
                                    Queued(
                                        cur.qud!!.addedAt, cur.qud!!.link, cur.qud!!.date,
                                        med.user.pk, med.user.username,
                                        car.pk, car.nearest(Versioned.BEST),
                                        car.thumb(), car.media_type.toInt().toByte(),
                                        dur = car.video_duration?.toLong(),
                                        caption = med.caption?.text
                                    )
                                )

                            med.image_versions2 != null -> cur.qud!!.apply {
                                date = med.taken_at.xFromSeconds()
                                userId = med.user.pk
                                userName = med.user.username
                                itemId = med.pk
                                url = med.nearest(Versioned.BEST)
                                thumb = med.thumb()
                                mediaType = med.media_type.toInt().toByte()
                                dur = med.video_duration?.toLong()
                                caption = med.caption?.text
                            }

                            else -> found = false
                        }
                        if (found) handleQueued(cur.qud!!, addOns)
                        else {
                            linkHandled()
                            if (download?.active != true) finish(false)
                        }
                    }
                    return@findFromHtml; }
                // ELSE IF IT'S A STORY/HIGHLIGHT or an invalid link...

                val root = ((cnfWrapper.require.keys
                    .find { it.startsWith("CometPlatformRootClient") }
                    ?.let { cnfWrapper.require[it] }
                    ?.getOrNull(2) as? List<Any>)
                    ?.getOrNull(0) as? Map<String, Map<String, Any?>>)
                    ?.get("initialRouteInfo")?.get("route")?.let {
                        Gson().fromJson(Gson().toJson(it), PageConfig.PolarisRoot::class.java)
                    }
                if (root == null) {
                    Api.gotError(this@Queuer, handler, null, null, HANDLE_HTML_ERROR)
                    if (System.getenv("test") == "1")
                        openFileOutput("${cur.qud?.addedAt}.json", 0)
                            .use { it.write(Gson().toJson(cnfWrapper).encodeToByteArray()) }
                    return@findFromHtml; }

                when (root.rootView.resource.__dr) {
                    "PolarisStoriesV3Root.react",
                    "PolarisStoriesMediaRoot.react" -> reqQueue.adder =
                        Api<Rest.Reels<Rest.StoryReel>>(
                            this, Api.Endpoint.REEL_ITEM.url.format(root.rootView.props.user_id),
                            Rest.Reels::class, handler, autoQueue = false, cache = true,
                            typeToken = object : TypeToken<Rest.Reels<Rest.StoryReel>>() {}.type
                        ) { reels ->
                            val rel = reels.reels.getOrNull(root.rootView.props.user_id)
                            val med = rel?.items?.find { it.pk == root.params.initial_media_id }
                            if (med == null) {
                                handler?.obtainMessage(HANDLE_API_RES_ERROR)
                                    ?.sendToTarget(); return@Api; }
                            cur.qud!!.apply {
                                date = med.taken_at.xFromSeconds()
                                userId = rel.user.pk
                                userName = rel.user.username
                                itemId = med.pk
                                url = med.nearest(Versioned.BEST)
                                thumb = med.thumb()
                                mediaType = med.media_type.toInt().toByte()
                                dur = med.video_duration?.toLong()
                                caption = med.caption?.text
                            }
                            handleQueued(cur.qud!!, null)
                        }

                    "PolarisStoriesV3HighlightsRoot.react",
                    "PolarisStoriesMediaHighlightsRoot.react" -> reqQueue.adder =
                        Api<Rest.Reels<Rest.HighlightReel>>(
                            this, Api.Endpoint.REEL_ITEM.url.format(
                                "highlight%3A${root.params.highlight_reel_id}"
                            ), Rest.Reels::class, handler, autoQueue = false, cache = true,
                            typeToken = object : TypeToken<Rest.Reels<Rest.HighlightReel>>() {}.type
                        ) { reels ->
                            val rel = reels.reels.getOrNull(
                                "highlight:${root.params.highlight_reel_id}"
                            )
                            val med = rel?.items?.find {
                                it.id == cur.link.substringAfter("story_media_id=")
                                    .substringBefore("&")
                            }
                            if (med == null) {
                                handler?.obtainMessage(HANDLE_API_RES_ERROR)
                                    ?.sendToTarget(); return@Api; }
                            cur.qud!!.apply {
                                date = med.taken_at.xFromSeconds()
                                userId = rel.user.pk
                                userName = rel.user.username
                                itemId = med.pk
                                url = med.nearest(Versioned.BEST)
                                thumb = med.thumb()
                                mediaType = med.media_type.toInt().toByte()
                                dur = med.video_duration?.toLong()
                                caption = med.caption?.text
                            }
                            handleQueued(cur.qud!!, null)
                        }
                    // Instagram cannot distinguish between contents of a private account and
                    // link of a public account itself; therefore this case should not be applied.
                    /*"PolarisProfileRoot.react" -> {
                        CoroutineScope(Dispatchers.IO)
                            .launch { dao.deleteQueued(cur.qud!!) }
                            .invokeOnCompletion { linkHandled() }
                    }*/
                    "PolarisLoginRoot.react", "PolarisChallengeRoot.react" ->
                        needAuthentication()

                    else -> {
                        if (System.getenv("test") == "1" && root.rootView.resource.__dr !in arrayOf(
                                "PolarisErrorRoot.react", "PolarisProfileRoot.react"
                            )
                        ) {
                            openFileOutput("unknown_api_${cur.qud?.addedAt}.json", 0)
                                .use { it.write(Gson().toJson(cnfWrapper).encodeToByteArray()) }
                            throw Exception(root.rootView.resource.__dr)
                        }
                    }
                }
            }
        }
    }
}
