package ir.mahdiparastesh.instatools.api

@Suppress("PropertyName")
data class Story(
    val cover_media: Cover?, // null in stories
    val id: String, // user id
    var items: List<Media>?, // null in highlights tray
    //val latest_reel_media: Double, // time in seconds
    val muted: Boolean?, // null in highlights
    //val reel_type: String, // "user_reel" or "highlight_reel"
    //val seen: Float?, // null in highlights
    val title: String?, // null in stories
    //val user: User
) {

    data class Cover(
        val cropped_image_version: Url,
        val full_image_version: Any?
    )

    data class Url(val url: String)

    data class Wrapper(val reels_media: List<Story>)
}