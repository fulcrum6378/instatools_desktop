package ir.mahdiparastesh.instatools

import com.google.gson.reflect.TypeToken
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.api.Rest

suspend fun main() {
    val api = Api()
    api.call<Rest.LazyList<Media>>(
        Api.Endpoint.MEDIA_INFO.url.format("3545298805687592771"), Rest.LazyList::class,
        typeToken = object : TypeToken<Rest.LazyList<Media>>() {}.type,
    ) { info ->
        println(info.items[0].nearest(Media.BEST))
    }
}
