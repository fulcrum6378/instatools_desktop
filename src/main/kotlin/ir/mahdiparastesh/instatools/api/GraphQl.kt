package ir.mahdiparastesh.instatools.api

@Suppress("PropertyName", "SpellCheckingInspection")
data class GraphQl(
    val data: GraphQlData?,
    //val errors: List<Map<String, Any>>?,
    //val extensions: Map<String, Any>,
    override val status: String // can be "ok" with HTTP code 200 while having errors!!!
) : Rest {

    data class GraphQlData(
        val highlights: Page<Story>?,
        val user: User?,
        val xdt_api__v1__feed__user_timeline_graphql_connection: Page<Media>?, // profile posts
        val xdt_api__v1__feed__reels_media: Story.Wrapper?, // daily stories
        val xdt_api__v1__feed__reels_media__connection: Page<Story>?, // highlighted stories
        //val xdt_api__v1__media__shortcode__web_info: MediaShortcodeWebInfo?,
        val xdt_api__v1__usertags__user_id__feed_connection: Page<Media>?, // tagged posts
    )

    data class Page<Node>(
        val edges: List<Edge<Node>>,
        val page_info: PageInfo,
    )

    data class Edge<Node>(val node: Node)

    data class PageInfo(
        //val end_cursor: String,
        val has_next_page: Boolean,
        //val has_previous_page: Boolean,
        //val start_cursor: String?,
    )
}
