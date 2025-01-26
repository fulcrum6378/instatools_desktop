package ir.mahdiparastesh.instatools.util

import ir.mahdiparastesh.instatools.InvalidCommandException
import ir.mahdiparastesh.instatools.api.Media

enum class Option(val key: String, val value: Any? = null) {
    QUALITY("q"),
    UNSAVE("u"),
    TYPE("t"),

    // exporting
    EXP_ALL_MEDIA("all-media"),
    EXP_IMAGES("images"),
    EXP_VIDEOS("videos"),
    EXP_POSTS("posts"),
    EXP_REELS("reels"),
    EXP_STORY("story"),
    EXP_UPLOADED_IMAGES("uploaded-images"),
    EXP_UPLOADED_VIDEOS("uploaded-videos"),
    EXP_VOICE("voice"),
    EXP_MIN_DATE("min-date"),
    EXP_MAX_DATE("max-date");


    companion object {
        fun parse(
            options: List<String>, selector: ((key: String) -> Option?)
        ): HashMap<String, String?> {
            val opt = hashMapOf<String, String?>()
            for (kv in options) {
                val kvSplit = if ("=" in kv) kv.split("=") else null
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

        fun directExportOptions(key: String): Option? = when (key) {
            "-u", "u", "--unsave", "-unsave", "unsave" -> UNSAVE
            "-q", "q", "--quality", "-quality", "quality" -> QUALITY
            "-t", "t", "--type", "-type", "type" -> TYPE
            "--all-media", "-all-media", "all-media" -> EXP_ALL_MEDIA
            "--images", "-images", "images", "--image", "-image", "image" -> EXP_IMAGES
            "--videos", "-videos", "videos", "--video", "-video", "video" -> EXP_VIDEOS
            "--posts", "-posts", "posts", "--post", "-post", "post" -> EXP_POSTS
            "--reels", "-reels", "reels", "--reel", "-reel", "reel" -> EXP_REELS
            "--story", "-story", "story", "--stories", "-stories", "stories" -> EXP_STORY
            "--uploaded-images", "-uploaded-images", "uploaded-images" -> EXP_UPLOADED_IMAGES
            "--uploaded-videos", "-uploaded-videos", "uploaded-videos" -> EXP_UPLOADED_VIDEOS
            "--voice", "-voice", "voice" -> EXP_VOICE
            "--min-date", "-min-date", "min-date" -> EXP_MIN_DATE
            "--max-date", "-max-date", "max-date" -> EXP_MAX_DATE
            else -> null
        }
    }
}
