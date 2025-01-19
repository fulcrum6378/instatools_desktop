package ir.mahdiparastesh.instatools.srv

import io.ktor.client.request.*
import io.ktor.client.statement.*
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class Queuer(val api: Api) {
    private var active = false
    private val queue = CopyOnWriteArrayList<Queued>()
    private val downloads = File("./downloads/")
    private val tiffDate = SimpleDateFormat("yyyy:MM:dd kk:mm:ss", Locale.getDefault())

    init {
        if (!downloads.isDirectory) downloads.mkdir()
    }

    /** Enqueues a media to be downloaded. */
    suspend fun enqueue(med: Media, link: String? = null) {
        val u = med.owner ?: med.user!!
        if (med.carousel_media != null) for (car in med.carousel_media) queue.add(
            Queued(
                car.pk,
                Utils.convertSecondsToMS(med.taken_at),
                Utils.rationaliseTimestamp(car.device_timestamp),
                car.nearest(Media.BEST)!!,
                car.media_type.toInt().toByte(),
                u.username,
                med.caption!!.text,
                link ?: med.link(),
                //car.thumb()
            )
        ) else queue.add(
            Queued(
                med.pk,
                Utils.convertSecondsToMS(med.taken_at),
                Utils.rationaliseTimestamp(med.device_timestamp),
                med.nearest(Media.BEST)!!,
                med.media_type.toInt().toByte(),
                u.username,
                med.caption!!.text,
                link ?: med.link(),
                //med.thumb()
            )
        )

        if (!active) withContext(Dispatchers.IO) { download() }
    }

    /** Downloads a media and saves it. */
    private suspend fun download() {
        active = true
        var fn: String
        while (queue.isNotEmpty()) queue.first().also { q ->
            fn = q.fileName()
            //api.client.get(q.url!!).bodyAsChannel().copyAndClose(File(downloads, fn).writeChannel())
            val ba = api.client.get(q.url).bodyAsBytes()
            val fos = FileOutputStream(File(downloads, fn))
            when (q.mediaType) {
                Media.Type.IMAGE.inDb -> ExifRewriter().updateExifMetadataLossless(
                    ba,
                    BufferedOutputStream(fos),
                    ((Imaging.getMetadata(ba) as JpegImageMetadata?)?.exif?.outputSet
                        ?: TiffOutputSet()).apply {
                        orCreateRootDirectory.apply {
                            removeField(TiffTagConstants.TIFF_TAG_IMAGE_DESCRIPTION) // Title + Subject
                            if (q.link != null)
                                add(TiffTagConstants.TIFF_TAG_IMAGE_DESCRIPTION, q.link)

                            removeField(ExifTagConstants.EXIF_TAG_SOFTWARE)
                            add(ExifTagConstants.EXIF_TAG_SOFTWARE, Utils.APP_NAME)

                            removeField(TiffTagConstants.TIFF_TAG_ARTIST) // Authors
                            add(TiffTagConstants.TIFF_TAG_ARTIST, q.userName)

                            removeField(TiffTagConstants.TIFF_TAG_COPYRIGHT)
                            add(TiffTagConstants.TIFF_TAG_COPYRIGHT, "IG: @${q.userName}")
                        }
                        orCreateExifDirectory.apply {
                            removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)
                            if (q.dateTaken != null) add( // Date taken
                                ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL,
                                tiffDate.format(q.dateTaken)
                            )

                            q.caption.also {
                                removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT)
                                add(ExifTagConstants.EXIF_TAG_USER_COMMENT, it)
                            }

                            removeField(ExifTagConstants.EXIF_TAG_SITE)
                            add(ExifTagConstants.EXIF_TAG_SITE, q.link)
                        }
                    }) // location data is currently not possible with edge post location.
                else -> fos.write(ba)
            }
            fos.close()
            println("Downloaded $fn")
            queue.removeFirst()
        }
        active = false
    }

    /** Data structure for information of a media. */
    data class Queued(
        val id: String,
        val datePosted: Long,
        val dateTaken: Long?,
        val url: String,
        val mediaType: Byte,
        val userName: String,
        val caption: String,
        val link: String?,
        //val thumb: String?,
    ) {
        fun fileName() = "${userName}_${Utils.fileDateTime(datePosted)}_" +
                "$id.${Media.Type.entries.find { it.inDb == mediaType }!!.ext}"
    }
}
