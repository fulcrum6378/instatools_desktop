package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import java.net.URLEncoder
import java.util.*

@Suppress("SpellCheckingInspection")
fun main() {
    val api = Api()
    val variables = "{" +
            "\"data\":" +
            "{" +
            "\"count\":12" +
            /*"\"include_reel_media_seen_timestamp\":true," +
            "\"include_relationship_info\":true," +
            "\"latest_besties_reel_media\":true," +
            "\"latest_reel_media\":true" +*/
            "}," +
            "\"username\":\"toni.marie.graham\"," +
            "\"__relay_internal__pv__PolarisIsLoggedInrelayprovider\":true" +
            "}"
    val pBody = /*"av=17841408323042127" +
            "&__d=www" +
            "&__user=0" +
            "&__a=1" +
            "&__req=7" +
            "&__hs=20112.HYP%3Ainstagram_web_pkg.2.1.0.0.1" +
            "&dpr=1" +
            "&__ccg=UNKNOWN" +
            "__rev=1019567637" +
            "&__s=11eo0g%3Akxk10f%3Atgfmno" +
            "&__hsi=7463603033595891995" +
            "&__dyn=7xeUjG1mxu1syUbFp41twpUnwgU7SbzEdF8aUco2qwJxS0k24o1DU2_CwjE1EE2Cw8G11wBz81s8hwGxu786a3a1YwBgao6C0Mo2swtUd8-U2zxe2GewGw9a361qw8Xxm16wUwtE1uVEbUGdG1QwTU9UaQ0Lo6-3u2WE5B08-269wr86C1mwPwUQp1yUb8jK5V8aUuwm8jxK2K0P8KmUhw" +
            "&__csr=gV2I888QPh5NG5QIIynbjIzlWZkBpiqAHAhaF4hFqmS9C-laihGRaGGdmiqA8GFpdeGUyGHV4qh7iAzHx2qJ4lah169KiivF6yuVaBJ34m9CF6yFecz8kCGih2oyim8qhbyFVpLGESai-Enz8x1eqc-iEx4xfzt1y4-EWiazXQEybG00i-i0jK4Zwd3w47g8U2HyFN4E33wW4wdu1lBguGcxi18wGxBwh80Yy2a0dZw2f8jg5mewe3EE2JgkBzUBp1zgAg2q2qr8ap41sw-CIyyV4crwcUzDglG1Fwwwa668b42a2ahS0se9Iwa0DNMjQ12xO060VJw0n2U1SE0lFw" +
            "&__comet_req=7" +
            "&fb_dtsg=NAcN35WEvNI44tOzOqYncFaT0oDWtGfQLwbo-J4t5anG8bGKtTYD7EA%3A17858449030071790%3A1737712569" +
            "&jazoest=26096" +
            "&lsd=r3y2fU67Rq2qj-mrKoVtOP" +
            "&__spin_r=1019567637" +
            "&__spin_b=trunk" +
            "&__spin_t=" + (Calendar.getInstance().timeInMillis / 1000).toString() +  //1737751406
            "&fb_api_caller_class=RelayModern" +
            "&fb_api_req_friendly_name=PolarisProfilePostsQuery" +*/
            "variables=${URLEncoder.encode(variables, "utf-8")}" +
            //"&server_timestamps=true" +
            "&doc_id=8934560356598281"
    api.call<GraphQl>(Api.Endpoint.QUERY.url, GraphQl::class, true, pBody)
        .data?.xdt_api__v1__media__shortcode__web_info
}
