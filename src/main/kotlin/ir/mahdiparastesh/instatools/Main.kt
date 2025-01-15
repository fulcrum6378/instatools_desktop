package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.json.*

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
e, export <LINK>             Export a conversation via its link.
p, profile <USER>            Get information about a user's profile. (e.g. p fulcrum6378)
i, info <ID>                 Find a user using their unique Instagram ID number. (e.g. i 8337021434)
q, quit                      Quit the program.

    """.trimIndent()
    )

    // preparations
    val api = Api()
    if (!api.loadCookies())
        System.err.println("No cookies found; insert cookies in `cookies.txt` right beside this JAR...")
    val downloader = Downloader(api)

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
                else System.err.println("Such file doesn't exist!")
            }

            "d", "download" -> if (a.size != 2)
                System.err.println("Invalid command!")
            else downloader.handleLink(a[1])  // TODO

            "e", "export" -> {
                // TODO
            }

            "p", "profile" ->  if (a.size != 2)
                System.err.println("Invalid command!")
            else api.call<GraphQl>(
                Api.Endpoint.PROFILE.url.format(a[1]), GraphQl::class
            ) { graphQl ->
                val u = graphQl.data?.user ?: return@call
                println(u.id)
            }

            "i", "info" -> if (a.size != 2)
                System.err.println("Invalid command!")
            else api.call<Rest.UserInfo>(
                Api.Endpoint.INFO.url.format(a[1]), Rest.UserInfo::class
            ) { info ->
                println(info.user.username)
            }

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
