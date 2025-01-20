package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.Rest
import ir.mahdiparastesh.instatools.job.Queuer

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
d, download <LINK>           Download only a post or reel via its official link.
s, saved                     Continuously list your saved posts.
    s <NUMBER>               Download the post in that index.
    s <NUMBER> [u|unsave]    Download + unsave the post in that index.
    s reset                  Forget the previously loaded saved posts and load them again. (update)
    s [u|unsave] <NUMBER>    Unsave the post in that index.
    s [r|resave] <NUMBER>    Save the post in that index AGAIN.
m, messages                  Lists your direct message threads.
p, profile <USER>            Get information about a user's profile. (e.g. p fulcrum6378)
u, user <ID>                 Find a user's name using their unique Instagram REST ID number. (e.g. u 8337021434)
q, quit                      Quit the program.

    """.trimIndent()
    )

    // preparations
    val api = Api()
    if (!api.loadCookies())
        System.err.println("No cookies found; insert cookies in `cookies.txt` right beside this JAR...")
    val queuer = Queuer(api)
    val c = Controller(api, queuer)

    // execute commands
    var repeat = true
    var nothing = 0
    while (repeat) {
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
                else System.err.println("Such a file doesn't exist!")
            }

            "d", "download" -> if (a.size != 2)
                System.err.println("Please enter a link after \"${a[0]}\"; like \"${a[0]} https://\"...")
            else if ("/p/" in a[1] || "/reel/" in a[1])
                c.handlePostLink(a[1])
            else
                System.err.println("Only links to Instagram posts and reels are supported!")

            "s", "saved" -> if (a.size == 1)
                c.listSavedPosts()
            else when (a[1]) {
                "reset" -> c.listSavedPosts(true)

                "u", "unsave", "r", "resave" -> c.getSavedPost(a[2])?.also { med ->
                    c.saveUnsave(med, a[1] == "u" || a[1] == "unsave")
                }

                else -> c.getSavedPost(a[1])?.also { med ->
                    queuer.enqueue(med)
                    a.getOrNull(2)?.also { addition ->
                        if (addition == "u" || addition == "unsave")
                            c.saveUnsave(med, true)
                        else
                            System.err.println("Unknown additional command \"$addition\"!")
                    }
                }
            }

            "m", "messages" -> {
                api.call<Rest.InboxPage>(
                    Api.Endpoint.INBOX.url.format(/*c.mm.dmInbox?.oldest_cursor ?:*/""),
                    Rest.InboxPage::class,
                ) { inbox ->
                }
                // TODO
            }

            "p", "profile" -> if (a.size != 2)
                System.err.println("Invalid command!")
            else api.call<GraphQl>(
                Api.Endpoint.PROFILE.url.format(a[1]), GraphQl::class
            ) { graphQl ->
                val u = graphQl.data?.user ?: return@call
                println(u.id)
            }

            "u", "user" -> if (a.size != 2)
                System.err.println("Invalid command!")
            else api.call<Rest.UserInfo>(
                Api.Endpoint.INFO.url.format(a[1]), Rest.UserInfo::class
            ) { info -> println("@${info.user.username}") }

            "q", "quit" -> repeat = false

            "" -> {
                nothing++
                if (nothing == 3) repeat = false
                else continue
            }

            else -> System.err.println("Unknown command: ${a[0]}")
        }
        if (a[0] != "") nothing = 0
    }

    api.client.close()
    println("Good luck!")
}

/*TO-DO
 *
 * [https://github.com/fulcrum6378/instatools/tree/master/app/src/kotlin/ir/mahdiparastesh/instatools]
 */
