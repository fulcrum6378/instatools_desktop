package ir.mahdiparastesh.instatools.util

import ir.mahdiparastesh.instatools.api.GraphQlQuery
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.job.SimpleJobs

object SimpleActions {
    fun likeMedia(med: Media, graphQlQuery: GraphQlQuery) {
        val unlike = graphQlQuery == GraphQlQuery.UNLIKE_STORY
        if (!canDoLiking(med, unlike)) return
        SimpleJobs.likeMedia(med, graphQlQuery) { success ->
            likeMessage(med, unlike, success)
        }
    }

    fun likePost(med: Media, unlike: Boolean = false) {
        if (!canDoLiking(med, unlike)) return
        SimpleJobs.likePost(med, unlike) { success ->
            likeMessage(med, unlike, success)
        }
    }

    private fun canDoLiking(med: Media, unlike: Boolean): Boolean =
        if ((!unlike && med.has_liked == true) || (unlike && med.has_liked == false)) {
            println("Already ${if (unlike) "un" else ""}liked ${med.link()}")
            true
        } else false

    private fun likeMessage(med: Media, unlike: Boolean, success: Boolean) {
        if (success)
            println("Successfully ${if (unlike) "un" else ""}liked ${med.link()}")
        else
            System.err.println("Could not ${if (unlike) "un" else ""}like ${med.link()}")
    }
}
