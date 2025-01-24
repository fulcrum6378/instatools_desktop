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

    fun directExportOptions(key: String): Option? = when (key) {
        "-u", "u", "--unsave", "-unsave", "unsave" -> Option.UNSAVE
        "-q", "q", "--quality", "-quality", "quality" -> Option.QUALITY
        "-t", "t", "--type", "-type", "type" -> Option.TYPE
        "--all-media", "-all-media", "all-media" -> Option.EXP_ALL_MEDIA
        "--images", "-images", "images", "--image", "-image", "image" -> Option.EXP_IMAGES
        "--videos", "-videos", "videos", "--video", "-video", "video" -> Option.EXP_VIDEOS
        "--posts", "-posts", "posts", "--post", "-post", "post" -> Option.EXP_POSTS
        "--reels", "-reels", "reels", "--reel", "-reel", "reel" -> Option.EXP_REELS
        "--story", "-story", "story", "--stories", "-stories", "stories" -> Option.EXP_STORY
        "--uploaded-images", "-uploaded-images", "uploaded-images" -> Option.EXP_UPLOADED_IMAGES
        "--uploaded-videos", "-uploaded-videos", "uploaded-videos" -> Option.EXP_UPLOADED_VIDEOS
        "--voice", "-voice", "voice" -> Option.EXP_VOICE
        "--min-date", "-min-date", "min-date" -> Option.EXP_MIN_DATE
        "--max-date", "-max-date", "max-date" -> Option.EXP_MAX_DATE
        else -> null
    }

    interface InstaToolsException
}
