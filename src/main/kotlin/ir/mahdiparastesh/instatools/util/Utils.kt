package ir.mahdiparastesh.instatools.util

import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object Utils {
    const val APP_NAME = "InstaTools CLI"
    const val MAHDI = "https://mahdiparastesh.ir/"
    const val POST_LINK = "https://www.instagram.com/p/%s/"
    const val REEL_LINK = "https://www.instagram.com/reel/%s/"
    const val STORY_LINK = "https://www.instagram.com/stories/%1\$s/%2\$s"
    const val GRAPHQL_POST_HASH = "8c2a529969ee035a5063f2fc8602a0fd"

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

    /** Converts a timestamp of seconds to a timestamp of millisecond. */
    fun convertSecondsToMS(seconds: Double) = seconds.toLong() * 1000L

    /** Rationalise irregular timestamps. */
    fun rationaliseTimestamp(ts: Double?): Long? {
        if (ts == null) return null
        var time = ts.toLong()

        // convert to milliseconds and add an omitted zero if needed
        when (time.toString().length) {
            9 -> time = (time.toString() + "0000").toLong()
            10 -> time *= 1000L // seconds
            12 -> time = (time.toString() + "0").toLong()
            13 -> {} // milliseconds
            15 -> time /= 100L
            16 -> time /= 1000L
            else -> return null
        }

        // rationalise
        val cal = Calendar.getInstance()
        if (time > cal.timeInMillis) return null
        val thisYear = cal[Calendar.YEAR]
        cal.timeInMillis = time
        if ((thisYear - cal[Calendar.YEAR]) > 9) return null

        return time
    }
}
