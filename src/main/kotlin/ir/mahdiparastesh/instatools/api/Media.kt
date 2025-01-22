package ir.mahdiparastesh.instatools.api

import ir.mahdiparastesh.instatools.util.Utils
import kotlin.math.abs

@Suppress("PropertyName")
data class Media(
    val can_reply: Boolean?,
    val caption: Caption?,
    val carousel_media: List<Media>?,
    val carousel_media_count: Float?,
    val carousel_media_ids: List<String>?,
    val coauthor_producers: List<User>?,
    val code: String?,
    val comment_count: Float?,
    val has_audio: Boolean?,
    val id: String, // <media ID>_<user ID>
    val invited_coauthor_producers: List<User>?,
    val image_versions2: ImageVersions2,
    val like_count: Double?,
    val location: Map<String, Any?>?,
    val media_type: Float,
    val number_of_qualities: Float?,
    val organic_tracking_token: String?,
    val original_height: Float,
    val original_width: Float,
    val owner: User?,
    val photo_of_you: Boolean?,
    val pk: String,
    val product_type: String?,
    val taken_at: Double,
    val user: User?,
    val video_dash_manifest: String?,
    val video_versions: List<Version>?,
    val view_count: Double?,
) {
    data class ImageVersions2(
        val candidates: List<Version>
    )

    data class Version(
        val url: String,
        val height: Float,
        val width: Float,
    )

    data class Caption(
        val created_at: Double,
        val pk: String,
        val text: String,
        val user: User?,
        val user_id: String?,
    )


    companion object {
        const val WORST = 0f
        const val MEDIUM = -1f
        const val BEST = -2f
        // Any positive number except these, represents an ideal width,
        // Any negative number except these, represents an ideal height.
    }

    fun owner(): User = owner ?: user!!

    fun link() = when (product_type) {
        "feed", "carousel_container" -> Utils.POST_LINK.format(code)
        "clips" -> Utils.REEL_LINK.format(code)
        "story" -> Utils.STORY_LINK.format(owner().username, pk)
        null -> nearest(BEST)
        else -> throw IllegalStateException("New product type: $product_type ?!?")
    }

    fun nearest(ideal: Float = BEST, justImage: Boolean = false): String? {
        var ret: String? = null
        if (!justImage && video_versions != null)
            ret = funChooser(video_versions, ideal)
        if (ret == null)
            ret = funChooser(image_versions2.candidates, ideal)
        return ret
    }

    private fun funChooser(list: List<Version>, ideal: Float): String? = when (ideal) {
        BEST -> bestOfList(list)
        MEDIUM -> mediumOfList(list)
        WORST -> worstOfList(list)
        else -> nearestOfList(list, ideal)
    }

    private fun bestOfList(list: List<Version>): String? {
        var ret: String?
        ret = list.find { it.width == original_width && it.height == original_height }?.url
        if (ret == null) {
            var maxW = 0f
            var maxH = 0f
            list.forEach {
                if (it.width > maxW) maxW = it.width
                if (it.height > maxH) maxH = it.height
            }
            ret = list.find { it.width == maxW && it.height == maxH }?.url
        }
        return ret
    }

    private fun nearestOfList(list: List<Version>, ideal: Float): String? {
        var nW = original_width
        var nH = original_height
        var nWDif = abs(ideal - nW)
        var nHDif = abs(ideal - nH)
        if (ideal > 0) list.forEach {
            if (abs(ideal - it.width) >= nWDif) return@forEach
            nWDif = abs(ideal - it.width)
            nW = it.width
            nH = it.height
        } else list.forEach {
            val idealH = abs(ideal)
            if (abs(idealH - it.height) >= nHDif) return@forEach
            nHDif = abs(idealH - it.height)
            nW = it.height
            nH = it.width
        }
        return list.find { it.width == nW && it.height == nH }?.url
            ?: list.getOrNull(0)?.url
    }

    private fun mediumOfList(list: List<Version>): String? =
        list.getOrNull(if (list.size <= 1) 0 else list.size / 2)?.url


    private fun worstOfList(list: List<Version>): String? {
        var minW = 1000f
        var minH = 1000f
        list.forEach {
            if (it.width < minW) minW = it.width
            if (it.height < minH) minH = it.height
        }
        return list.find { it.width == minW && it.height == minH }?.url
            ?: list.getOrNull(0)?.url
    }

    fun thumb() = //(this as Media).thumbnails?.sprite_urls?.getOrNull(0)
        carousel_media?.getOrNull(0)?.nearest(WORST, true) ?: nearest(WORST, true)

    fun hasAudio() =
        has_audio == true || (carousel_media != null && carousel_media.any { it.media_type == 2f })

    fun audioUrl(): String? {
        if (video_dash_manifest == null) return null
        return video_dash_manifest
            .substringAfter("<AudioChannelConfiguration")
            .substringAfter("<BaseURL")
            .substringAfter(">")
            .substringBefore("</BaseURL>")
    }

    enum class Type(val num: Byte, val ext: String) {
        IMAGE(1, "jpg"), // could be PNG as well
        VIDEO(2, "mp4"),
        AUDIO(3, "m4a"),
    }
}
