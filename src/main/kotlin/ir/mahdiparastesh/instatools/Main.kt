package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.Context.downloader
import ir.mahdiparastesh.instatools.Context.exporter
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.Rest
import ir.mahdiparastesh.instatools.job.Downloader
import ir.mahdiparastesh.instatools.job.Exporter
import ir.mahdiparastesh.instatools.list.Direct
import ir.mahdiparastesh.instatools.list.Posts
import ir.mahdiparastesh.instatools.list.Saved
import ir.mahdiparastesh.instatools.list.Tagged
import ir.mahdiparastesh.instatools.util.SimpleTasks
import ir.mahdiparastesh.instatools.util.Utils
import java.net.URI
import java.net.URISyntaxException

object Context {
    val api: Api by lazy { Api() }
    val downloader: Downloader by lazy { Downloader() }
    val exporter: Exporter by lazy { Exporter() }
}

fun main(args: Array<String>) {
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
set proxy {URL}              Set an HTTP proxy (e.g. `set proxy http://127.0.0.1:8580/`)
set timeout <seconds>        Set timeout for normal HTTP requests (not downloads) (e.g. `set timeout 10`)

>> List of commands:
d, download <LINK> {OPTIONS}   Download only a post or reel via its official link.
    -q, --quality=<QUALITY>              A valid quality value (e.g. -q=high) (defaults to high)
s, saved                       Continuously list your saved posts.
  s <NUMBER(s)> {OPTIONS}      Download the post in that index.
    -q, --quality=<QUALITY>              A valid quality value (e.g. -q=high) (defaults to high)
    -u, --unsave                         Additionally unsave the post
  s reset                      Forget the previously loaded saved posts and load them again.
  s [u|unsave] <NUMBER>        Unsave the post in that index.
  s [r|resave] <NUMBER>        Save the post in that index AGAIN.
m, messages                    List your direct message threads.
  m <NUMBER(s)> {OPTIONS}      Export the thread in that index.
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
  m reset                      Forget the previously loaded threads and load them again.
u, user <USER|REST_ID>         Show details about an IG account. (e.g. u 8337021434)
p, posts <USERNAME>            List main posts of a profile. (e.g. p fulcrum6378)
  p reset                      Forget the previously loaded main posts and load them again.
t, tagged <USERNAME>           List tagged posts of a profile. (e.g. t fulcrum6378)
  t reset                      Forget the previously loaded tagged posts and load them again.
q, quit                        Quit the program.

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
    val listSvd: Saved by lazy { Saved() }
    val listMsg: Direct by lazy { Direct() }
    val listPst: Posts by lazy { Posts() }
    val listTag: Tagged by lazy { Tagged() }

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

                "proxy" -> try {
                    api.client = api.createClient(proxy = URI(a[2]))
                } catch (_: URISyntaxException) {
                    throw InvalidCommandException("Please enter a valid URI like the example above.")
                }

                "timeout" -> {
                    val sec = try {
                        a[2].toInt()
                    } catch (_: NumberFormatException) {
                        throw InvalidCommandException("Please enter a valid number.")
                    }
                    if (sec < 0)
                        throw InvalidCommandException("Please enter a positive number.")
                    api.client = api.createClient(timeout = sec * 1000)
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
                listSvd.fetch()
            else when (a[1]) {
                "reset" -> listSvd.fetch(true)

                "u", "unsave", "r", "resave" -> listSvd[a[2]]?.forEach { med ->
                    listSvd.saveUnsave(med, a[1] == "u" || a[1] == "unsave")
                }

                else -> {
                    listSvd[a[1]]?.forEach { med ->
                        val opt = Utils.options(a.getOrNull(2)) { key ->
                            when (key) {
                                "-u", "u", "--unsave", "-unsave", "unsave" -> Option.UNSAVE
                                "-q", "q", "--quality", "-quality", "quality" -> Option.QUALITY
                                else -> null
                            }
                        }
                        downloader.enqueue(med, Utils.quality(opt?.get(Option.QUALITY.key)))
                        if (opt?.contains(Option.UNSAVE.key) == true)
                            listSvd.saveUnsave(med, true)
                    }
                    downloader.start()
                }
            }

            "m", "messages" -> if (a.size == 1)
                listMsg.fetch()
            else when (a[1]) {
                "reset" -> listMsg.fetch(true)

                else -> {
                    listMsg[a[1]]?.forEach { thread ->
                        val opt = Utils.options(a.getOrNull(2), Utils::directExportOptions)
                            ?: throw InvalidCommandException("Please specify options for the export.")
                        exporter.enqueue(thread, opt)
                    }
                }
            }

            "u", "user" -> if (a.size != 2)
                throw InvalidCommandException()
            else try {
                a[1].toLong()
                api.call<Rest.UserInfo>(Api.Endpoint.USER_INFO.url.format(a[1]), Rest.UserInfo::class).user
            } catch (_: NumberFormatException) {
                api.call<GraphQl>(Api.Endpoint.PROFILE.url.format(a[1]), GraphQl::class).data!!.user!!
            }.also { u ->
                println(
                    """
Full name:        ${u.full_name}
Username:         @${u.username}
Unique REST ID:   ${u.id()}
Pronouns:         ${u.pronouns?.joinToString(", ")}
Is private?       ${if (u.is_private == true) "Yes" else "No"}
Bio:
${u.biography}

                """.trimIndent()
                )
            }

            "p", "posts" -> if (a.size != 2)
                throw InvalidCommandException()
            else {
                // TODO listPst, reset
            }

            "t", "tagged" -> if (a.size != 2)
                throw InvalidCommandException()
            else {
                // TODO listTag, reset
            }

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
        System.err.println(e.message)
    } catch (e: Api.FailureException) {
        System.err.println(e.message)
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

class InvalidCommandException(msg: String = "Invalid command!") :
    IllegalArgumentException(msg)
