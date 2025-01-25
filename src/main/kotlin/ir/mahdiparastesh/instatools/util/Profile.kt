package ir.mahdiparastesh.instatools.util

import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.list.Posts
import ir.mahdiparastesh.instatools.list.Tagged

class Profile(var userName: String) {
    var userId: String? = null

    val posts: Posts by lazy { Posts(this) }
    val tagged: Tagged by lazy { Tagged(this) }

    fun requireUserId() {
        if (userId != null) return
        userId = api.call<GraphQl>(Api.Endpoint.PROFILE.url.format(userName), GraphQl::class).data!!.user!!.id!!
        if (System.getenv("debug") == "1") println("Found the user ID: $userId")
    }
}
