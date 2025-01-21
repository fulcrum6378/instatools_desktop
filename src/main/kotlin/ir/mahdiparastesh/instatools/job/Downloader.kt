package ir.mahdiparastesh.instatools.job

import io.ktor.client.request.*
import io.ktor.client.statement.*
import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.util.Queuer
import ir.mahdiparastesh.instatools.util.Utils
import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

/** Downloads media and saves them. */
class Downloader : Queuer<Downloader.Queued>() {
    override val outputDir = File("./downloads/")

    suspend fun download(med: Media, idealSize: Float, link: String? = null) {
        val u = med.owner()
        if (med.carousel_media != null) for (car in med.carousel_media) enqueue(
            Queued(
                car.pk,
                Utils.compileSecondsTS(car.taken_at),
                car.nearest(idealSize)!!,
                car.media_type.toInt().toByte(),
                u.username,
                med.caption!!.text,
                link ?: med.link(),
                //car.thumb()
            )
        ) else enqueue(
            Queued(
                med.pk,
                Utils.compileSecondsTS(med.taken_at),
                med.nearest(idealSize)!!,
                med.media_type.toInt().toByte(),
                u.username,
                med.caption!!.text,
                link ?: med.link(),
                //med.thumb()
            )
        )
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun handle(q: Queued) {
        val fileName = q.fileName()
        val file = File(outputDir, fileName)
        // TODO if (f.exists())
        val ba = api.client.get(q.url).bodyAsBytes()
        val fos = FileOutputStream(file)
        when (q.type) {
            Media.Type.IMAGE.inDb -> ExifRewriter().updateExifMetadataLossless(
                ba, BufferedOutputStream(fos), ((Imaging.getMetadata(ba) as JpegImageMetadata?)?.exif?.outputSet
                    ?: TiffOutputSet()).apply {
                    orCreateRootDirectory.apply {
                        removeField(TiffTagConstants.TIFF_TAG_IMAGE_DESCRIPTION) // Title + Subject
                        if (q.link != null) add(TiffTagConstants.TIFF_TAG_IMAGE_DESCRIPTION, q.link)
                        removeField(ExifTagConstants.EXIF_TAG_SOFTWARE)
                        add(ExifTagConstants.EXIF_TAG_SOFTWARE, Utils.APP_NAME)
                        removeField(TiffTagConstants.TIFF_TAG_ARTIST) // Authors
                        add(TiffTagConstants.TIFF_TAG_ARTIST, q.owner)
                        removeField(TiffTagConstants.TIFF_TAG_COPYRIGHT)
                        add(TiffTagConstants.TIFF_TAG_COPYRIGHT, "IG: @${q.owner}")
                    }
                    orCreateExifDirectory.apply {
                        q.caption.also {
                            removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT)
                            add(ExifTagConstants.EXIF_TAG_USER_COMMENT, it)
                        }
                        removeField(ExifTagConstants.EXIF_TAG_SITE)
                        add(ExifTagConstants.EXIF_TAG_SITE, q.link)
                    }
                }) // location data is currently not possible with edge post location.
            // TODO metadata for videos
            else -> fos.write(ba)
        }
        fos.close()
        println("Downloaded $fileName")
    }

    /** Data structure for information of a media. */
    data class Queued(
        val id: String,
        val date: Long,
        val url: String,
        val type: Byte,
        val owner: String,
        val caption: String,
        val link: String?,
        //val thumb: String?,
    ) {
        fun fileName() = "${owner}_${Utils.fileDateTime(date)}_" +
                "$id.${Media.Type.entries.find { it.inDb == type }!!.ext}"
    }
}
