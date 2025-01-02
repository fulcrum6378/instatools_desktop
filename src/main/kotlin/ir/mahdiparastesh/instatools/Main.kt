package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.json.Api
import ir.mahdiparastesh.instatools.json.Rest

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

i, info <ID>:                Find a user using their unique Instagram ID number.      e.g. i 8337021434
q, quit:                     Quit the program.

    """.trimIndent()
    )

    // preparations
    val api = Api()

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
