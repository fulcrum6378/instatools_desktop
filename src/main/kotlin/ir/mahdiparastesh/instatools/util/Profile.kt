package ir.mahdiparastesh.instatools.util

import ir.mahdiparastesh.instatools.list.Posts
import ir.mahdiparastesh.instatools.list.Tagged

class Profile(username: String) {
    val posts: Posts by lazy { Posts(username) }
    val tagged: Tagged by lazy { Tagged(username) }

    interface Lister {
        val username: String
    }
}
