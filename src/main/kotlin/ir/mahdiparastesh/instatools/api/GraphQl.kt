package ir.mahdiparastesh.instatools.api

@Suppress("PropertyName", "SpellCheckingInspection")
class GraphQl(
    val data: GraphQlData?,
    //val extensions: Map<String, Any>,
    override val status: String
) : Rest {

    data class GraphQlData(
        val user: User?,
        val xdt_api__v1__feed__user_timeline_graphql_connection: Page?,
        val xdt_api__v1__media__shortcode__web_info: MediaShortcodeWebInfo?,
        val xdt_api__v1__usertags__user_id__feed_connection: Page?,
    )

    data class Page(
        val edges: List<Post>,
        val page_info: PageInfo,
    )

    data class Post(val node: Media)

    data class PageInfo(
        val end_cursor: String,
        val has_next_page: Boolean,
        //val has_previous_page: Boolean,
        //val start_cursor: String?,
    )

    class MediaShortcodeWebInfo(val items: List<Media>)


    /*class Profile(
        //val always_show_message_button_to_pro_account: Boolean,
        //val graphql: GraphQlData?,
        //val logging_page_id: String,
        //val profile_pic_edit_sync_props: Map<String, *>,
        //val seo_category_infos: Array<Array<String>>,
        //val show_follow_dialog: Boolean,
        //val show_suggested_profiles: Boolean,
        //val show_view_shop: Boolean,
        //val toast_content_on_load: Any?
    )*/

    /*open class EdgeFollow(val count: Double) {
        override fun toString(): String = when {
            count > 1000000.0 -> DecimalFormat("#.##").format(count / 1000000.0) + "M"
            count > 1000.0 -> DecimalFormat("#.##").format(count / 1000.0) + "K"
            else -> count.toInt().toString()
        } // Cannot move to strings.xml without Context
    }*/

    //class EdgeFollowMutual(count: Double, val edges: Array<Any>) : EdgeFollow(count)

    class Src(val src: String, val config_width: Double/*, val config_height: Double*/)

    class EdgeSlides(val edges: Array<EdgeSlide>)

    class EdgeSlide(val node: Slide)

    class Slide(
        //val __typename: String,
        val id: String,
        //val gating_info: Any?,
        //val fact_check_overall_rating: Any?,
        //val fact_check_information: Any?,
        //val media_overlay_info: Any?,
        //val sensitivity_friction_info: Any?,
        //val sharing_friction_info: Map<String, *>?,
        //val dimensions: Map<String, Double>?,
        //val display_url: String, // USE THIS
        //val display_resources: Array<Src>
    )

    /*class DashInfo(
        override val is_dash_eligible: Any?,
        override val video_dash_manifest: String?,
        override val number_of_qualities: Float?
    ) : Audible*/
}
