package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.Context.api
import ir.mahdiparastesh.instatools.Context.direct
import ir.mahdiparastesh.instatools.Context.queuer
import ir.mahdiparastesh.instatools.Context.saved
import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.Media
import ir.mahdiparastesh.instatools.api.Rest
import ir.mahdiparastesh.instatools.job.Queuer
import ir.mahdiparastesh.instatools.list.Direct
import ir.mahdiparastesh.instatools.list.Saved
import ir.mahdiparastesh.instatools.util.SimpleTasks

object Context {
    val api: Api by lazy { Api() }
    val queuer: Queuer by lazy { Queuer() }
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
>> List of commands:
c, cookies <PATH>            Load the required cookies from a path. (defaults to `./cookies.txt`)
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
    -t, --type=<HTML,PDF,TXT>              File type of the output export
    --all_media=<none|QUALITY>             Default settings for all media (e.g. --all_media=low)
    --images=<none|QUALITY>                Default settings for all images (e.g. --images=low)
    --videos=<none|thumb|QUALITY>          Default settings for all videos (e.g. --videos=thumb)
    --posts=<none|QUALITY>                 Settings for shared posts (e.g. --posts=none)
    --reels=<none|thumb|QUALITY>           Settings for shared reels (e.g. --reels=none)
    --uploaded_images=<none|QUALITY>       Settings for directly uploaded images  (e.g. --uploaded_images=high)
    --uploaded_videos=<none|thumb|QUALITY> Settings for directly uploaded videos  (e.g. --uploaded_videos=high)
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

            "c", "cookies" -> {
                if (if (a.size > 1) api.loadCookies(a[1]) else api.loadCookies())
                    println("Cookies loaded!")
                else
                    throw InvalidCommandException("Such a file doesn't exist!")
            }

            "d", "download" -> if (a.size >= 2)
                throw InvalidCommandException("Please enter a link after \"${a[0]}\"; like \"${a[0]} https://\"...")
            else if ("/p/" in a[1] || "/reel/" in a[1]) {
                val opt = options(a.getOrNull(2))
                SimpleTasks.handlePostLink(a[1], quality(opt?.get(Option.QUALITY.key)))
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
                    val opt = options(a.getOrNull(2))
                    queuer.enqueue(med, quality(opt?.get(Option.QUALITY.key)))
                    if (opt?.contains(Option.UNSAVE.key) == true)
                        saved.saveUnsave(med, true)
                }
            }

            "m", "messages" -> if (a.size == 1)
                direct.fetch()
            else when (a[1]) {
                "reset" -> direct.fetch(true)

                else -> direct[a[1]]?.also { thread ->
                    val opt = options(a.getOrNull(2))
                    // TODO
                    println(thread.exportFileName())
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

class InvalidCommandException(message: String = "Invalid command!") :
    IllegalArgumentException(message)

private fun options(raw: String?): HashMap<String, String?>? {
    if (raw == null) return null
    val opt = hashMapOf<String, String?>()
    for (kv in raw.split(" ")) {
        val kvSplit = if ("=" !in kv) kv.split("=") else null
        val k = kvSplit?.get(0) ?: kv

        val addable: Option? = when (k) {
            "-u", "u", "--unsave", "-unsave", "unsave" -> Option.UNSAVE
            "-q", "q", "--quality", "-quality", "quality" -> Option.QUALITY
            else -> null
        }
        if (addable == null)
            throw InvalidCommandException("Unknown option \"$k\"!")
        opt[addable.key] = kvSplit?.get(1)
    }
    return opt
}

private fun quality(value: String? = null): Float {
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

enum class Option(val key: String, val value: Any? = null) {
    QUALITY("q"),
    UNSAVE("u"),
}

/*TO-DO
 *
 * [https://github.com/fulcrum6378/instatools/tree/master/app/src/kotlin/ir/mahdiparastesh/instatools]
 */
