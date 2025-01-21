package ir.mahdiparastesh.instatools.util

import ir.mahdiparastesh.instatools.InvalidCommandException
import ir.mahdiparastesh.instatools.Option
import ir.mahdiparastesh.instatools.api.Media
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object Utils {
    const val APP_NAME = "InstaTools"
    const val MAHDI = "https://mahdiparastesh.ir/"
    const val POST_LINK = "https://www.instagram.com/p/%s/"
    const val REEL_LINK = "https://www.instagram.com/reel/%s/"
    const val STORY_LINK = "https://www.instagram.com/stories/%1\$s/%2\$s"
    const val GRAPHQL_POST_HASH = "8c2a529969ee035a5063f2fc8602a0fd"
    const val REST_STATUS_OK = "ok"

    fun options(
        raw: String?, selector: ((key: String) -> Option?)
    ): HashMap<String, String?>? {
        if (raw == null) return null
        val opt = hashMapOf<String, String?>()
        for (kv in raw.split(" ")) {
            val kvSplit = if ("=" !in kv) kv.split("=") else null
            val k = kvSplit?.get(0) ?: kv
            val addable: Option = selector(k)
                ?: throw InvalidCommandException("Unknown option \"$k\"!")
            opt[addable.key] = kvSplit?.getOrNull(1)
        }
        return opt
    }

    fun quality(value: String? = null): Float {
        if (value == null) return Media.BEST
        return when (value) {
            "h", "high", "original" -> Media.BEST
            "m", "medium", "med" -> Media.MEDIUM
            "l", "low" -> Media.WORST
            "x" -> try {
                value.substring(1).toFloat()
            } catch (_: NumberFormatException) {
                throw InvalidCommandException("\"$value\" is not a valid number!")
            }

            "y" -> try {
                -value.substring(1).toFloat()
            } catch (_: NumberFormatException) {
                throw InvalidCommandException("\"$value\" is not a valid number!")
            }

            else -> throw InvalidCommandException("Unknown quality \"$value\"!")
        }
    }

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
    fun compileSecondsTS(seconds: Double) = seconds.toLong() * 1000L

    /** Converts a timestamp of microseconds to a timestamp of millisecond. */
    fun compileMicrosecondsTS(microseconds: Double) = microseconds.toLong() / 1000L
}
