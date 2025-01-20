package ir.mahdiparastesh.instatools.api

@Suppress("SpellCheckingInspection", "PropertyName")
interface Rest {
    val status: String

    data class QuickResponse(override val status: String) : Rest

    data class LazyList<N>(
        val auto_load_more_enabled: Boolean,
        val items: List<N>,
        val more_available: Boolean,
        val next_max_id: String?,
        val num_results: Float, // in current fetch, not real total
        override val status: String,
    ) : Rest

    data class SavedItem(val media: Media)

    data class UserInfo(
        val user: User,
        override val status: String
    ) : Rest

    data class InboxPage(
        val viewer: User,
        val inbox: Message.Inbox,
        val seq_id: String,
        val snapshot_at_ms: Double, // milliseconds
        val pending_requests_total: Double,
        val has_pending_top_requests: Boolean,
        override val status: String
    ) : Rest

    /** Both following and followers receive this API. */
    data class Follow(
        val users: List<User>? = null,
        /* true for @fulcrum6378 which needs multiple fetches,
         * false for @instatools.apk which requires a single one. */
        //val big_list: Boolean,
        /* Maximum amount of users a single fetch can take which randomly is lower than expected!
         * always equals 200, even in Instagram Web's own fetches! */
        //val page_size: Double,
        val next_max_id: String? = null,
        /* "Accounts you don't follow back", "Least interacted with", etc. ONLY IN FOLLOWERS! */
        //val groups: Map<String, Any?>,
        /* Only in followers */
        //val more_groups_available: Boolean,
        //val has_more: Boolean, always returns false incorrectly!
        //val should_limit_list_of_followers: Boolean,
        override val status: String
    ) : Rest

    data class Friendships(
        val friendship_statuses: Map<String, User.FriendshipStatus>,
        override val status: String
    ) : Rest

    interface DynamicReelsList : Rest {
        //var broadcast: Array<Any?>? = null
    }

    //data class Story(val reel: StoryReel?) : DynamicReelsList()

    /*interface TrayWrapper<T> where T : Reel {
        val tray: Array<T>
    }*/

    /*data class Highlights(
        override val tray: Array<HighlightReel>,
        //val show_empty_state: Boolean,
    ) : Rest(), TrayWrapper<HighlightReel>*/

    /*data class Reels<R>(
        val reels: Map<String, R>,
        //val reels_media: Array<R>,
    ) : Rest() where R : Reel*/

    /*abstract class Reel(
        //val ad_expiry_timestamp_in_millis: Any?,
        //val can_gif_quick_reply: Boolean,
        //val can_reply: Boolean,
        //val can_reshare: Boolean,
        //val is_cta_sticker_available: Any?,
        var items: Array<MediaOld>?,
        //val latest_reel_media: Double,
        //val reel_type: String,
        //val seen: Double,
        val user: User
    )*/

    /*data class StoryReel(
        //val expiring_at: Double,
        //val has_besties_media: Boolean?,
        //val has_fan_club_media: Boolean?,
        //val id: Double, // User ID is the same as that of the reel!
        items: Array<MediaOld>,
        //val media_count: Float,
        //val media_ids: Array<String>,
        //val prefetch_count: Float,
        user: User
    ) : Reel(items, user)*/

    /*data class HighlightReel(
        val cover_media: HighlightCover?, // uncertain "?"
        //val created_at: Double,
        val id: String, // starts with "highlight:"
        //val is_converted_to_clips: Boolean,
        //val is_pinned_highlight: Boolean,
        items: Array<MediaOld>?,
        val media_count: Float,
        //val media_ids: Array<String>?,
        //val prefetch_count: Double,
        //val ranked_position: Double,
        //val seen_ranked_position: Double,
        val title: String,
        user: User
    ) : Reel(items, user)*/

    //data class HighlightCover(val cropped_image_version: MediaOld.Candidate/*, val crop_rect: Any?*/)

    data class Search(
        //val places: Array<HashMap<String, *>>,
        //val hashtags: Array<HashMap<String, *>>,
        //val rank_token: String,
        //val has_more: Boolean,
        val users: List<ItemUser>,
        override val status: String
    ) : Rest

    data class ItemUser(val position: Float, val user: User)

    data class Signing(
        //val login_nonce: String?
        override val status: String
    ) : Rest

    data class DoFollow(
        //val feedback_title: String?, // e.g.: "Try again later"
        //val feedback_message: String?, // e.g.: "We restrict certain activity to protect our community."
        //val feedback_url: String?,
        //val feedback_action: String?, // e.g.: "report_problem"
        //val friendship_status: FriendshipStatus,
        //val message: String?, // e.g.: "feedback_required"
        //val previous_following: Boolean?,
        //val result: String?,
        val spam: Boolean?,
        override val status: String
    ) : Rest

    data class Seen(val status_code: String /* must be "200" */)

    data class ApiFailure(
        //val message: String, // e.g.: "checkpoint_required"
        //val checkpoint_url: String, // e.g.: "https://www.instagram.com/challenge/?next=<THE_API_ENDPOINT>"
        val lock: Boolean,
        //val flow_render_type: Float, // e.g.: 0
        override val status: String // was "fail"
    ) : Rest
}
