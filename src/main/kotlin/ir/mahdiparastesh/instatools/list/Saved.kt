package ir.mahdiparastesh.instatools.list

import com.google.gson.reflect.TypeToken
import io.ktor.http.*
import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.api.Rest
import ir.mahdiparastesh.instatools.util.LazyLister
import ir.mahdiparastesh.instatools.util.Utils

class Saved : LazyLister<Media>() {

    override suspend fun fetch(reset: Boolean) {
        super.fetch(reset)
        api.call<Rest.LazyList<Rest.SavedItem>>(
            Api.Endpoint.SAVED.url + (if (cursor != null && !reset) "?max_id=$cursor" else ""),
            Rest.LazyList::class, typeToken = object : TypeToken<Rest.LazyList<Rest.SavedItem>>() {}.type,
        ) { lazyList ->
            for (i in lazyList.items) {
                println(
                    "$index. ${i.media.link()} - @${i.media.owner().username} : " +
                            "${i.media.caption?.text?.replace("\n", " ")}"
                )
                add(i.media)
            }
            if (lazyList.more_available) {
                cursor = lazyList.next_max_id
                println("Enter `s` again to load more posts...")
            } else endOfList()
        }
    }

    /** Saves or unsaves posts. */
    suspend fun saveUnsave(med: Media, unsave: Boolean) {
        api.call<Rest.QuickResponse>(
            (if (unsave) Api.Endpoint.UNSAVE else Api.Endpoint.SAVE).url.format(med.pk),
            Rest.QuickResponse::class, HttpMethod.Post
        ) { rest ->
            if (rest.status == Utils.REST_STATUS_OK)
                println("Successfully ${if (unsave) "unsaved" else "saved"} ${med.link()}")
            else
                System.err.println("Couldn't ${if (unsave) "unsave" else "save"} this post!")
        }
    }
}
