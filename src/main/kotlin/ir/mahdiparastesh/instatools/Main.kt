package ir.mahdiparastesh.instatools

import io.ktor.client.engine.*
import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.Context.direct
import ir.mahdiparastesh.instatools.Context.downloader
import ir.mahdiparastesh.instatools.Context.exporter
import ir.mahdiparastesh.instatools.Context.saved
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.Rest
import ir.mahdiparastesh.instatools.job.Downloader
import ir.mahdiparastesh.instatools.job.Exporter
import ir.mahdiparastesh.instatools.list.Direct
import ir.mahdiparastesh.instatools.list.Saved
import ir.mahdiparastesh.instatools.util.SimpleTasks
import ir.mahdiparastesh.instatools.util.Utils

object Context {
    val api: Api by lazy { Api() }
    val downloader: Downloader by lazy { Downloader() }
    val exporter: Exporter by lazy { Exporter() }
    val saved: Saved by lazy { Saved() }
    val direct: Direct by lazy { Direct() }
}

suspend fun main(args: Array<String>) {
    val interactive = args.isEmpty()
    println(
        """
InstaTools ${if (interactive) "interactive " else ""}command-line interface
Copyright © Mahdi Parastesh - All Rights Reserved.

    """.trimIndent()
    )

    if (interactive) println(
        """
>> List of settings:
set cookies {PATH}           Load the required cookies from a path. (defaults to `./cookies.txt`)
set proxy {URL}              Set an HTTP proxy (e.g. `http://127.0.0.1:8580/`)

>> List of commands:
d, download <LINK> {OPTIONS} Download only a post or reel via its official link.
    -q, --quality=<QUALITY>  A valid quality value (listed at the bottom) (e.g. -q=high) (defaults to high)
s, saved                     Continuously list your saved posts.
  s <NUMBER> {OPTIONS}       Download the post in that index.
    -q, --quality=<QUALITY>  A valid quality value (listed at the bottom) (e.g. -q=high) (defaults to high)
    -u, --unsave             Additionally unsave the post.
  s reset                    Forget the previously loaded saved posts and load them again. (update)
  s [u|unsave] <NUMBER>      Unsave the post in that index.
  s [r|resave] <NUMBER>      Save the post in that index AGAIN.
m, messages                  List your direct message threads.
  m <NUMBER> {OPTIONS}       Export the thread in that index.
    -t, --type=<HTML,TXT>                File type of the output export
    --all-media=<no|QUALITY>             Default settings for all media (e.g. --all-media=low)
    --images=<no|QUALITY>                Default settings for all images (e.g. --images=low)
    --videos=<no|thumb|QUALITY>          Default settings for all videos (e.g. --videos=thumb)
    --posts=<no|QUALITY>                 Settings for shared posts (e.g. --posts=no)
    --reels=<no|thumb|QUALITY>           Settings for shared reels (e.g. --reels=no)
    --story=<no|thumb|QUALITY>           Settings for shared stories and highlights (e.g. --story=no)
    --uploaded-images=<no|QUALITY>       Settings for directly uploaded images  (e.g. --uploaded-images=high)
    --uploaded-videos=<no|thumb|QUALITY> Settings for directly uploaded videos  (e.g. --uploaded-videos=high)
    --voice=<no|yes>                     Whether voice messages should be downloaded (e.g. --voice=yes)
    --min-date=<DATETIME>                Minimum date for messages to be exported (e.g. --min-date=2025-01-21)
    --max-date=<DATETIME>                Minimum date for messages to be exported (e.g. --min-date=2024)
  m reset                    Forget the previously loaded thread and load them again. (update)
p, profile <USER>            Get information about a user's profile. (e.g. p fulcrum6378)
u, user <ID>                 Find a user's name using their unique Instagram REST ID number. (e.g. u 8337021434)
q, quit                      Quit the program.

>> List of qualities:
h, high                        Highest available quality (original)
m, medium                      Medium quality
l, low                         Lowest available quality (often thumbnail for images)
x<NUMBER>                      Ideal width (e.g. x1000) (do NOT separate the number)
y<NUMBER>                      Ideal height (e.g. y1000) (do NOT separate the number)

    """.trimIndent()
    )

    // preparations
    if (!api.loadCookies())
        System.err.println("No cookies found; insert cookies in `cookies.txt` right beside this JAR...")

    // execute commands
    var repeat = true
    var nothing = 0
    while (repeat) try {
        val a: Array<String>
        if (interactive) {
            println("Type a command: ")
            a = readlnOrNull()?.split(" ")?.toTypedArray() ?: continue
            if (a.isEmpty()) continue
        } else {
            a = args
            repeat = false
        }

        when (a[0]) {

            "set" -> when (a.getOrNull(1)) {
                "cookies" -> {
                    if (if (a.size > 2) api.loadCookies(a[2]) else api.loadCookies())
                        println("Cookies loaded!")
                    else
                        throw InvalidCommandException("Such a file doesn't exist!")
                }

                "proxy" -> {
                    api.client.engine.config.proxy =
                        if (a.size > 2) ProxyBuilder.http(a[2]) else null
                    println("Proxy = " + a[2])
                }

                null -> throw InvalidCommandException("Invalid setting!")
            }

            "d", "download" -> if (a.size >= 2)
                throw InvalidCommandException(
                    "Please enter a link after \"${a[0]}\"; like \"${a[0]} https://\"..."
                )
            else if ("/p/" in a[1] || "/reel/" in a[1]) {
                val opt = Utils.options(a.getOrNull(2)) { key ->
                    when (key) {
                        "-q", "q", "--quality", "-quality", "quality" -> Option.QUALITY
                        else -> null
                    }
                }
                SimpleTasks.handlePostLink(a[1], Utils.quality(opt?.get(Option.QUALITY.key)))
            } else
                throw InvalidCommandException("Only links to Instagram posts and reels are supported!")

            "s", "saved" -> if (a.size == 1)
                saved.fetch()
            else when (a[1]) {
                "reset" -> saved.fetch(true)

                "u", "unsave", "r", "resave" -> saved[a[2]]?.also { med ->
                    saved.saveUnsave(med, a[1] == "u" || a[1] == "unsave")
                }

                else -> saved[a[1]]?.also { med ->
                    val opt = Utils.options(a.getOrNull(2)) { key ->
                        when (key) {
                            "-u", "u", "--unsave", "-unsave", "unsave" -> Option.UNSAVE
                            "-q", "q", "--quality", "-quality", "quality" -> Option.QUALITY
                            else -> null
                        }
                    }
                    downloader.download(med, Utils.quality(opt?.get(Option.QUALITY.key)))
                    if (opt?.contains(Option.UNSAVE.key) == true)
                        saved.saveUnsave(med, true)
                }
            }

            "m", "messages" -> if (a.size == 1)
                direct.fetch()
            else when (a[1]) {
                "reset" -> direct.fetch(true)

                else -> direct[a[1]]?.also { thread ->
                    val opt = Utils.options(a.getOrNull(2)) { key ->
                        when (key) {
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
                    } ?: throw InvalidCommandException("Please specify options for the export.")
                    exporter.enqueue(thread, opt)
                }
            }

            "p", "profile" -> if (a.size != 2)
                throw InvalidCommandException()
            else api.call<GraphQl>(
                Api.Endpoint.PROFILE.url.format(a[1]), GraphQl::class
            ) { graphQl ->
                val u = graphQl.data?.user ?: return@call
                println(u.id)
            }

            "u", "user" -> if (a.size != 2)
                throw InvalidCommandException()
            else api.call<Rest.UserInfo>(
                Api.Endpoint.INFO.url.format(a[1]), Rest.UserInfo::class
            ) { info -> println("@${info.user.username}") }

            "q", "quit" -> repeat = false

            "" -> {
                nothing++
                if (nothing == 3) repeat = false
                else continue
            }

            else -> throw InvalidCommandException("Unknown command: ${a[0]}")
        }
        if (a[0] != "") nothing = 0
    } catch (e: InvalidCommandException) {
        System.err.println(e)
    }

    api.client.close()
    println("Good luck!")
}

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
    EXP_MAX_DATE("max-date"),
}

class InvalidCommandException(message: String = "Invalid command!") :
    IllegalArgumentException(message)
