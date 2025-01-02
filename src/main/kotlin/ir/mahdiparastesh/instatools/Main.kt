package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.json.Api
import ir.mahdiparastesh.instatools.json.Rest

suspend fun main() { // args: Array<String>
    val api = Api()
    api.call<Rest.UserInfo>(
        Api.Endpoint.INFO.url.format("8337021434"), Rest.UserInfo::class
    ) { info ->
        println(info.user.visName())
    }
    api.client.close()
}

/*TODO
 *
 * [https://github.com/fulcrum6378/instatools/tree/master/app/src/kotlin/ir/mahdiparastesh/instatools]
 */
