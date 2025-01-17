package ir.mahdiparastesh.instatools

import com.google.gson.Gson
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.Api.Companion.xFromSeconds
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.api.RelayPrefetchedStreamCache
import java.io.File

class Downloader(private val api: Api) {
    private val queue = arrayListOf<Queued>()

    suspend fun handleLink(link: String) {
        api.page(link, { status ->
            when (status) {
                429 -> throw IllegalStateException("Too man requests!")
                404 -> throw IllegalStateException("Error 404!")
                else -> throw IllegalStateException("Error $status!")
            }
        }) { html ->
            //println(html)
            //return@page
            val data = RelayPrefetchedStreamCache.crawl(html)
            println("Found: " + data.keys.joinToString(", "))

            // Post, Reel, TV TODO TV?!
            if ("PolarisPostRootQueryRelayPreloader" in data) {
                @Suppress("UNCHECKED_CAST")
                val medMap = (data["PolarisPostRootQueryRelayPreloader"]!!["items"] as List<Map<String, Any>>)[0]
                val med = Gson().fromJson(Gson().toJson(medMap), Media.Post::class.java)

                when {
                    med.carousel_media != null -> for (car in med.carousel_media) queue.add(
                        Queued(
                            link,
                            med.taken_at.xFromSeconds(),
                            med.user.pk,
                            med.user.username,
                            car.pk,
                            car.nearest(Media.BEST),
                            car.thumb(),
                            car.media_type.toInt().toByte(),
                            med.caption.text
                        )
                    )

                    else -> queue.add(
                        Queued(
                            link,
                            med.taken_at.xFromSeconds(),
                            med.user.pk,
                            med.user.username,
                            med.pk,
                            med.nearest(Media.BEST),
                            med.thumb(),
                            med.media_type.toInt().toByte(),
                            med.caption.text
                        )
                    )
                }
                download()
            }

            /*val root = ((cnfWrapper.require.keys
                .find { it.startsWith("CometPlatformRootClient") }
                ?.let { cnfWrapper.require[it] }
                ?.getOrNull(2) as? List<Any>)
                ?.getOrNull(0) as? Map<String, Map<String, Any?>>)
                ?.get("initialRouteInfo")?.get("route")?.let {
                    Gson().fromJson(Gson().toJson(it), RelayPrefetchedStreamCache.PolarisRoot::class.java)
                }
                ?: throw IllegalStateException("Couldn't extract configurations from HTML!")

            when (root.rootView.resource.__dr) {
                "PolarisStoriesV3Root.react",
                "PolarisStoriesMediaRoot.react" -> api.call<Rest.Reels<Rest.StoryReel>>(
                    Api.Endpoint.REEL_ITEM.url.format(root.rootView.props.user_id), Rest.Reels::class,
                    typeToken = object : TypeToken<Rest.Reels<Rest.StoryReel>>() {}.type
                ) { reels ->
                    val rel = reels.reels.getOrElse(root.rootView.props.user_id) { null }
                    val med = rel?.items?.find { it.pk == root.params.initial_media_id }
                        ?: throw IllegalStateException("med == null")
                    queue.add(
                        Queued(
                            link,
                            med.taken_at.xFromSeconds(),
                            rel.user.pk,
                            rel.user.username,
                            med.pk,
                            med.nearest(Versioned.BEST),
                            med.thumb(),
                            med.media_type.toInt().toByte(),
                            med.caption?.text
                        )
                    )
                    download()
                }

                "PolarisStoriesV3HighlightsRoot.react",
                "PolarisStoriesMediaHighlightsRoot.react" -> api.call<Rest.Reels<Rest.HighlightReel>>(
                    Api.Endpoint.REEL_ITEM.url.format("highlight%3A${root.params.highlight_reel_id}"),
                    Rest.Reels::class, typeToken = object : TypeToken<Rest.Reels<Rest.HighlightReel>>() {}.type
                ) { reels ->
                    val rel = reels.reels.getOrElse("highlight:${root.params.highlight_reel_id}") { null }
                    val med = rel?.items?.find {
                        it.id == link.substringAfter("story_media_id=").substringBefore("&")
                    } ?: throw IllegalStateException("med == null")

                    queue.add(
                        Queued(
                            link,
                            med.taken_at.xFromSeconds(),
                            rel.user.pk,
                            rel.user.username,
                            med.pk,
                            med.nearest(Versioned.BEST),
                            med.thumb(),
                            med.media_type.toInt().toByte(),
                            med.caption?.text,
                        )
                    )
                    download()
                }
                // Instagram cannot distinguish between contents of a private account and
                // link of a public account itself; therefore this case should not be applied.
                *//*"PolarisProfileRoot.react" -> {
                    CoroutineScope(Dispatchers.IO)
                        .launch { dao.deleteQueued(cur.qud!!) }
                        .invokeOnCompletion { linkHandled() }
                }*//*
                "PolarisLoginRoot.react", "PolarisChallengeRoot.react" ->
                    throw IllegalStateException("You are logged out!")

                else -> {
                    if (System.getenv("test") == "1" && root.rootView.resource.__dr !in
                        arrayOf("PolarisErrorRoot.react", "PolarisProfileRoot.react")
                    ) throw IllegalStateException("Unknown response ${root.rootView.resource.__dr}")
                }
            }*/
        }
    }

    private suspend fun download() {
        val downloads = File("./downloads/")
        if (!downloads.isDirectory) downloads.mkdir()

        var fn: String
        queue.forEach { q ->
            fn = q.fileName()
            api.client.get(q.url!!).bodyAsChannel().copyAndClose(File(downloads, fn).writeChannel())
            println("Downloaded $fn")
        }
        queue.clear()
    }

    data class Queued(
        val link: String,
        var date: Long,
        var userId: String,
        var userName: String? = null,
        var itemId: String? = null,
        var url: String? = null,
        var thumb: String? = null,
        var mediaType: Byte,
        var caption: String? = null,
    ) {
        fun fileName() = "${userName}_${Utils.fileDateTime(date)}_" +
                "$itemId.${MediaType.entries.find { it.inDb == mediaType }!!.ext}"
    }

    enum class MediaType(val ext: String, val inDb: Byte) {
        PHOTO("jpg", 1),
        VIDEO("mp4", 2),
        AUDIO("m4a", 3),
    }
}
