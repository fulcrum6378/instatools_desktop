package ir.mahdiparastesh.instatools.api

@Suppress("PropertyName")
interface Rest {
    val status: String

    data class QuickResponse(override val status: String) : Rest

    data class LazyList<N>(
        //val auto_load_more_enabled: Boolean,
        val items: List<N>,
        val more_available: Boolean,
        val next_max_id: String?,
        //val num_results: Float, // in current fetch, not real total
        override val status: String,
    ) : Rest

    data class SavedItem(val media: Media)

    data class UserInfo(
        val user: User,
        override val status: String
    ) : Rest

    data class InboxPage(
        //val viewer: User,
        val inbox: Message.Inbox,
        //val seq_id: String,
        //val snapshot_at_ms: Double, // milliseconds
        //val pending_requests_total: Double,
        //val has_pending_top_requests: Boolean,
        override val status: String
    ) : Rest

    data class InboxThread(
        val thread: Message.DmThread,
        override val status: String
    ) : Rest
}
