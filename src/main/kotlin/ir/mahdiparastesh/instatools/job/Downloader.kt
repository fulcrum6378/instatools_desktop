package ir.mahdiparastesh.instatools.job

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
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI

/** Downloads media and saves them. */
class Downloader : Queuer<Downloader.Queued>() {
    override val outputDir = File("./Downloads/")

    fun download(med: Media, idealSize: Float, link: String? = null) {
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

    override fun handle(q: Queued) {
        val extension = q.extension()
        val fileName = q.fileName(extension)
        val file = File(outputDir, fileName)
        if (file.exists()) {
            println("File `${fileName}` already exists! Overwrite? (y / any)")
            if (readlnOrNull() !in arrayOf("y", "Y", "yes")) return
        }
        var response: CloseableHttpResponse? = null
        while (response?.statusLine?.statusCode != 200) {
            if (response != null)
                println("Retrying for ${q.link}")

            val request = HttpGet(q.url).apply {
                config = RequestConfig.custom().setConnectTimeout(
                    when (q.type) {
                        Media.Type.IMAGE.num -> 15000
                        else -> 2 * 60000
                    }
                ).build()
            }
            try {
                response = api.client.execute(request)
            } catch (e: IOException) {
                throw e  // FIXME
            }
        }
        val ba = response.entity.content.readAllBytes()
        val fos = FileOutputStream(file)
        when (extension) {
            "jpg" -> ExifRewriter().updateExifMetadataLossless(
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

            else -> fos.write(ba) // TODO metadata for videos, exclude PNG and there could also be others
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
        fun fileName(ext: String) = "${owner}_${Utils.fileDateTime(date)}_$id.$ext"

        fun extension() = URI(url).path.split(".").lastOrNull()
            ?: Media.Type.entries.find { it.num == type }!!.ext
    }
}
