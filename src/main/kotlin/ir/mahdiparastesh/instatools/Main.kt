package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import ir.mahdiparastesh.instatools.api.Rest
import ir.mahdiparastesh.instatools.srv.Queuer

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
c, cookies <PATH>            Load cookies from `cookies.txt` or you can specify another file.
d, download <LINK|PATH>      Download post via their single links or multiple links inside a text file.
s, saved {reset|number}      List saved posts
e, export <LINK>             Export a conversation via its link.
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
                System.err.println("Invalid command!")
            else if ("/p/" in a[1] || "/reel/" in a[1])
                c.handlePostLink(a[1])
            else
                System.err.println("Only links to posts and reel are supported!")

            "s", "saved" -> if (a.size == 1)
                c.listSavedPosts()
            else if (a[1] == "reset")
                c.listSavedPosts(true)
            else {
                try {
                    queuer.enqueue(c.savedPosts[a[1].toInt() - 1])
                } catch (e: Exception) {
                    System.err.println("Invalid command: ${e::class.simpleName}")
                }
                // TODO UNSAVE
            }

            "e", "export" -> {
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
