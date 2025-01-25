package ir.mahdiparastesh.instatools.list

import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.util.LazyLister
import ir.mahdiparastesh.instatools.util.Profile

class Tagged(override val username: String) : LazyLister<Media>(), Profile.Lister {

    override fun fetch(reset: Boolean) {
        super.fetch(reset)
    }
}
