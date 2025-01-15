package ir.mahdiparastesh.instatools.view

import java.util.*

object UiTools {
    const val POST_LINK = "https://www.instagram.com/p/%s/"
    const val STORY_LINK = "https://www.instagram.com/stories/%1\$s/%2\$s"
    const val HIGHLIGHT_LINK = "https://www.instagram.com/stories/highlights/%s/" // inexact
    const val REEL_LINK = "https://www.instagram.com/reel/%s/"
    const val IGTV_LINK = "https://www.instagram.com/tv/%s/"

    /** Helper class for turning 1 to "01". */
    fun z(n: Int): String {
        val s = n.toString()
        return if (s.length == 1) "0$s" else s
    }

    /** @return a datetime text to be used in a file name. */
    fun fileDateTime(time: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = time }
        return "${cal[Calendar.YEAR]}${z(cal[Calendar.MONTH] + 1)}" +
                "${z(cal[Calendar.DAY_OF_MONTH])}_${z(cal[Calendar.HOUR_OF_DAY])}" +
                "${z(cal[Calendar.MINUTE])}${z(cal[Calendar.SECOND])}"
    }
}
