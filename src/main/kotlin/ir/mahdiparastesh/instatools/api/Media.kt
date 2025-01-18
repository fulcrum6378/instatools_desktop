package ir.mahdiparastesh.instatools.api

import kotlin.math.abs

@Suppress("PropertyName", "SpellCheckingInspection")
interface Media {
    val accessibility_caption: String?
    val carousel_parent_id: String?
    val display_uri: String?
    val has_liked: Boolean
    val id: String
    val image_versions2: ImageVersions2
    val inventory_source: Any?
    val is_dash_eligible: Any? // sometimes boolean sometimes double(0|1)
    val like_count: Double?
    val link: Any?
    val logging_info_token: Any?
    val media_overlay_info: Any?
    val media_type: Float
    val number_of_qualities: Float?
    val organic_tracking_token: String?
    val original_height: Float
    val original_width: Float
    val owner: MediaOwner?
    val pk: String
    val preview: String?
    val previous_submitter: Any?
    val sharing_friction_info: Map<String, Any?>
    val story_cta: Any?
    val taken_at: Double
    val user: MediaUser?
    val usertags: Map<String, Any?>
    val video_dash_manifest: String?
    val video_versions: List<VideoVersion>?


    data class MediaInfo(
        override val taken_at: Double,
        override val pk: String,
        override val id: String,
        val fbid: String,
        val device_timestamp: Double,
        val caption_is_edited: Boolean,
        val strong_id__: String,
        val deleted_reason: Float,
        val has_shared_to_fb: Float,
        val has_delayed_metadata: Boolean,
        val mezql_token: String,
        val share_count_disabled: Boolean,
        val is_visual_reply_commenter_notice_enabled: Boolean,
        val like_and_view_counts_disabled: Boolean,
        val is_post_live_clips_media: Boolean,
        val can_modify_carousel: Boolean,
        val is_quiet_post: Boolean,
        val comment_threading_enabled: Boolean,
        val is_unified_video: Boolean,
        val commerciality_status: String,
        val client_cache_key: String,
        val integrity_review_decision: String,
        val should_request_ads: Boolean,
        val is_reshare_of_text_post_app_media_in_ig: Boolean,
        val has_privately_liked: Boolean,
        val filter_type: Float,
        override val usertags: Map<String, List<Any>>,
        val photo_of_you: Boolean,
        val can_see_insights_as_brand: Boolean,
        override val media_type: Float,
        val code: String,
        val caption: Caption, // FIXME
        override val sharing_friction_info: Map<String, Any?>,
        val timeline_pinned_user_ids: List<Any>,
        val has_views_fetching: Boolean,
        override val accessibility_caption: String,
        val original_media_has_visual_reply_media: Boolean,
        val fb_user_tags: Map<String, List<Any>>,
        val coauthor_producers: List<MediaAuthor>,
        val coauthor_producer_can_see_organic_insights: Boolean,
        val invited_coauthor_producers: List<MediaAuthor>,

    ): Media

    data class Post(
        val code: String,
        override val pk: String,
        override val id: String,
        val ad_id: Any?,
        override val taken_at: Double,
        override val inventory_source: Any?,
        override val video_versions: List<VideoVersion>?,
        val coauthor_producers: List<MediaAuthor>,
        val invited_coauthor_producers: List<MediaAuthor>,
        val facepile_top_likers: List<Any>,
        override val is_dash_eligible: Float?,
        override val number_of_qualities: Float?,
        override val video_dash_manifest: String?,
        override val image_versions2: ImageVersions2,
        val is_paid_partnership: Boolean,
        val sponsor_tags: Any?,
        override val original_height: Float,
        override val original_width: Float,
        override val organic_tracking_token: String,
        override val user: MediaUser,
        val group: Any?,
        val comments_disabled: Boolean?,
        val like_and_view_counts_disabled: Boolean,
        val can_viewer_reshare: Boolean,
        val product_type: String,
        override val media_type: Float,
        override val usertags: Map<String, List<Any>>,
        override val media_overlay_info: Any?,
        val carousel_media: List<CarouselItem>?,
        val location: Map<String, Any?>?,
        val has_audio: Boolean?,
        val clips_metadata: Any?,
        val clips_attribution_info: Any?,
        override val has_liked: Boolean,
        val open_carousel_submission_state: String?,
        override val carousel_parent_id: String?,
        override val display_uri: String?,
        override val accessibility_caption: String?,
        override val previous_submitter: Any?,
        override val link: Any?,
        override val story_cta: Any?,
        override val like_count: Double,
        override val logging_info_token: Any?,
        override val owner: MediaOwner,
        override val preview: String?,
        val carousel_media_count: Float?,
        val comment_count: Float,
        val preview_comments: List<Any>,
        val view_count: Double?,
        val top_likers: List<Any>,
        val fb_like_count: Any?,
        val social_context: Any?,
        val can_reshare: Boolean?,
        val saved_collection_ids: List<Any>?,
        val has_viewer_saved: Boolean?,
        override val sharing_friction_info: Map<String, Any?>,
        val caption: Caption,
        val boosted_status: Any?,
        val boost_unavailable_identifier: Any?,
        val boost_unavailable_reason: Any?,
        val can_see_insights_as_brand: Boolean,
        val affiliate_info: Any?,
        val main_feed_carousel_starting_media_id: Any?,
        val media_level_comment_controls: Any?,
        val ig_media_sharing_disabled: Boolean,
        val feed_demotion_control: Any?,
        val feed_recs_demotion_control: Any?,
        val photo_of_you: Boolean,
        val all_previous_submitters: Any?,
        val follow_hashtag_info: Any?,
        val caption_is_edited: Boolean,
        val commenting_disabled_for_viewer: Any?,
    ) : Media {

        fun hasAudio() =
            has_audio == true || (carousel_media != null && carousel_media.any { it.media_type == 2f })
    }

    data class CarouselItem(
        override val pk: String,
        override val id: String,
        override val accessibility_caption: String?,
        override val is_dash_eligible: Float?,
        override val video_dash_manifest: String?,
        override val original_height: Float,
        override val original_width: Float,
        override val image_versions2: ImageVersions2,
        override val carousel_parent_id: String?,
        override val sharing_friction_info: Map<String, Any?>,
        override val preview: String?,
        override val organic_tracking_token: String?,
        override val video_versions: List<VideoVersion>?,
        override val media_overlay_info: Any?,
        override val usertags: Map<String, List<Any>>,
        override val media_type: Float,
        override val display_uri: String?,
        override val user: MediaUser?,
        override val number_of_qualities: Float?,
        override val taken_at: Double,
        override val previous_submitter: Any?,
        override val link: Any?,
        override val story_cta: Any?,
        override val has_liked: Boolean,
        override val like_count: Double?,
        override val inventory_source: Any?,
        override val logging_info_token: Any?,
        override val owner: MediaOwner?,
    ) : Media

    data class ImageVersions2(
        val candidates: List<ImageVersion>
    )

    interface Version {
        val url: String
        val height: Float
        val width: Float
    }

    data class ImageVersion(
        override val url: String,
        override val height: Float,
        override val width: Float,
    ) : Version

    data class VideoVersion(
        override val url: String,
        override val height: Float,
        override val width: Float,
        val type: Float,
    ) : Version

    data class MediaUser(
        override val pk: String,
        override val username: String,
        val full_name: String,
        override val profile_pic_url: String,
        val is_private: Boolean,
        val is_embeds_disabled: Boolean,
        val is_unpublished: Boolean,
        override val is_verified: Boolean,
        val friendship_status: Map<String, Any?>,
        val latest_reel_media: Double,
        override val id: String,
        val __typename: String,
        val live_broadcast_visibility: Any?,
        val live_broadcast_id: Any?,
        val hd_profile_pic_url_info: Map<String, Any?>,
    ) : Rest.User

    data class MediaOwner(
        override val pk: String,
        override val id: String,
        override val username: String,
        override val profile_pic_url: String,
        val show_account_transparency_details: Boolean,
        val __typename: String,
        val friendship_status: Map<String, Any?>,
        val transparency_product: Any?,
        val transparency_product_enabled: Boolean,
        val transparency_label: Any?,
        val is_unpublished: Boolean?,
        override val is_verified: Boolean,
    ) : Rest.User

    data class MediaAuthor(
        override val pk: String,
        override val profile_pic_url: String,
        val is_unpublished: Boolean?,
        override val username: String,
        override val id: String,
        val __typename: String,
        val full_name: String,
        override val is_verified: Boolean,
        val friendship_status: Map<String, Any?>?,
        val supervision_info: Any?,
    ) : Rest.User

    data class Caption(
        val text: String,
        val pk: String,
        val has_translation: Boolean?,
        val created_at: Double,
    )


    companion object {
        const val WORST = 0f
        const val MEDIUM = -1f
        const val BEST = -2f
        // Any positive number except these, represents an ideal width,
        // Any negative number except these, represents an ideal height.
    }

    fun nearest(ideal: Float = BEST, justImage: Boolean = false): String? {
        var ret: String? = null
        if (!justImage && video_versions != null)
            ret = funChooser(video_versions as List<Version>, ideal)
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
        (if (this is Post) carousel_media?.getOrNull(0)?.nearest(WORST, true) else null)
            ?: nearest(WORST, true)

    fun audioUrl(): String? {
        if (video_dash_manifest == null) return null
        return video_dash_manifest!!
            .substringAfter("<AudioChannelConfiguration")
            .substringAfter("<BaseURL")
            .substringAfter(">")
            .substringBefore("</BaseURL>")
    }
}
