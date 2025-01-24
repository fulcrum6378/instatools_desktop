package ir.mahdiparastesh.instatools.job

import ir.mahdiparastesh.instatools.InvalidCommandException
import ir.mahdiparastesh.instatools.Option
import ir.mahdiparastesh.instatools.api.Message
import ir.mahdiparastesh.instatools.util.Queuer
import java.io.File

/** Exports direct messages. */
class Exporter : Queuer<Exporter.Exportable>() {
    override val outputDir = File("./Messages/")

    fun enqueue(thread: Message.DmThread, opt: HashMap<String, String?>) {
        val allMedia = opt[Option.EXP_ALL_MEDIA.key]
        enqueue(
            Exportable(
                thread,
                1, // TODO
                setting(allMedia ?: opt[Option.EXP_IMAGES.key]),
                setting(allMedia ?: opt[Option.EXP_VIDEOS.key]),
                setting(allMedia ?: opt[Option.EXP_POSTS.key]),
                setting(allMedia ?: opt[Option.EXP_REELS.key]),
                setting(allMedia ?: opt[Option.EXP_STORY.key]),
                setting(allMedia ?: opt[Option.EXP_UPLOADED_IMAGES.key]),
                setting(allMedia ?: opt[Option.EXP_UPLOADED_VIDEOS.key]),
                when (opt[Option.EXP_VOICE.key]) {
                    "yes", "y", "1" -> true
                    "no", "n", "none" -> false
                    else -> throw InvalidCommandException("Please set `yes` or `no` for voice.")
                },
                dateTime(opt[Option.EXP_MIN_DATE.key]),
                dateTime(opt[Option.EXP_MAX_DATE.key]),
            )
        )
    }

    private fun setting(value: String?): Float? {
        if (value in arrayOf("no", "n", "none")) return null
        // Utils.quality() | thumb
        // TODO
        return null
    }

    private fun dateTime(value: String?): Long? {
        if (value == null) return null
        // TODO
        return null
    }

    override fun handle(q: Exportable) {
        // TODO
    }

    data class Exportable(
        val thread: Message.DmThread,
        val type: Byte,
        val image: Float?,
        val video: Float?,
        val post: Float?,
        val reel: Float?,
        val story: Float?,
        val uploadedImage: Float?,
        val uploadedVideo: Float?,
        val voice: Boolean,
        val min: Long?,
        val max: Long?,
    )
}
