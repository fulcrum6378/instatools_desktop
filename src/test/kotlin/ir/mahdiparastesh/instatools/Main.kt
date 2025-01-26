package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl

fun main() {
    val api = Api()

    api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        Api.GraphQlQuery.STORY.body("\"2003616263\"")
    ).data?.xdt_api__v1__feed__reels_media?.reels_media?.forEach { userStory ->
        userStory.items!!.forEachIndexed { i, story ->
            println("$i. " + story.link())
        }
    }
    println("\n\n")

    /*val hlIds = api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        Api.GraphQlQuery.PROFILE_HIGHLIGHTS_TRAY.body("2003616263")
    ).data?.highlights?.edges?.map { "\"${it.node.id}\"" }
    if (hlIds == null) throw Api.FailureException(-3)

    if (hlIds.isEmpty())
        println("No highlighted stories.")
    else*/ api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        "av=17841408323042127&" +
                "__d=www&" +
                "__user=0&" +
                "__a=1&" +
                "__req=2s&" +
                "__hs=20114.HYP%3Ainstagram_web_pkg.2.1.0.0.1&" +
                "dpr=1&" +
                "__ccg=UNKNOWN&" +
                "__rev=1019596031&" +
                "__s=9e4k8m%3Ae0r3e5%3Adc4kax&" +
                "__hsi=7464247596223464641&" +
                "__dyn=7xeUjG1mxu1syUbFp41twpUnwgU7SbzEdF8aUco2qwJxS0k24o1DU2_CwjE1EE2Cw8G11wBz81s8hwGxu786a3a1YwBgao6C0Mo2iyo7u3ifK0EUjwGzEaE2iwNwmE2eUlwhEe87q0nKq2-azqwt8d-2u2J0bS1LwTwKG1pg2fwxyo6O1FwlEcUed6goK2O4Xxui2K7E5y4UrwHwGwa6bBK4o&" +
                "__csr=gX5MV3cO6jNf2sAlbnsTIR8Bh17ldFbrcZ6ZpDgKvbAtGv9V9kECZVAirQUxGWiyFAhFaiiuUSmiiLGHxyjhaHQA9CyWAqRByV9rhe6pVammdAgS9mqjm5p4Ex3VER5HUiqAhV9m-ma_CyXiWy945UW4K-BGFpoW4Q5Bxum00jMK3-2Ewjwt19A8hk784R0JAg6G0gyhODxR1evK0D87y1FwkUe83Vw76wJw5yw1cG0K2ep218SdhA0Hyy8Za4-0C8n-10acwzjgmCF8IVi5wr86Cl2o9AmP385UMjwXG12wNwL4h85-5awkQ222fc1OgjBgog620BU9UdwwgEG7EfU0nIy8Cl00mZU0gywfG&" +
                "__comet_req=7&" +
                "fb_dtsg=NAcMrEnzFkEKp5rbYLTkpnaXiZRkzOgj1O9b3vQ0aP0W8ZqvBAsTf0w%3A17858449030071790%3A1737712569&" +
                "jazoest=26391&" +
                "lsd=h1DiMkOiHePdJPBzWJzrKO&" +
                "__spin_r=1019596031&" +
                "__spin_b=trunk&" +
                "__spin_t=1737905571&" +
                "fb_api_caller_class=RelayModern&" +
                "fb_api_req_friendly_name=PolarisStoriesV3HighlightsPageQuery&" +
                "server_timestamps=true&" +
                Api.GraphQlQuery.HIGHLIGHTS.body(
                    //hlIds.joinToString(","),
                    "\"highlight:18276957214204490\"," +
                            "\"highlight:17930958344608052\"," +
                            "\"highlight:17891630795726523\"," +
                            "\"highlight:18185612626165637\"",
                    //hlIds[0]
                    "\"highlight:18276957214204490\""
                )
    ).data?.xdt_api__v1__feed__reels_media__connection?.also { page ->
        page.edges.forEach { tray ->
            tray.node.items!!.forEachIndexed { i, story ->
                println("${tray.node.id} : $i. " + story.link())
            }
        }
    }// ?: throw Api.FailureException(-3)

    /*fetch("https://www.instagram.com/graphql/query", {
        "headers": {
        "accept": "**",
        "accept-language": "en-GB,en;q=0.9,fa-IR;q=0.8,fa;q=0.7,es-US;q=0.6,es;q=0.5,ru-RU;q=0.4,ru;q=0.3,de-DE;q=0.2,de;q=0.1,cs-CZ;q=0.1,cs;q=0.1,en-US;q=0.1",
        "content-type": "application/x-www-form-urlencoded",
        "priority": "u=1, i",
        "sec-ch-prefers-color-scheme": "light",
        "sec-ch-ua": "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"",
        "sec-ch-ua-full-version-list": "\"Google Chrome\";v=\"131.0.6778.265\", \"Chromium\";v=\"131.0.6778.265\", \"Not_A Brand\";v=\"24.0.0.0\"",
        "sec-ch-ua-mobile": "?0",
        "sec-ch-ua-model": "\"\"",
        "sec-ch-ua-platform": "\"Windows\"",
        "sec-ch-ua-platform-version": "\"10.0.0\"",
        "sec-fetch-dest": "empty",
        "sec-fetch-mode": "cors",
        "sec-fetch-site": "same-origin",
        "x-asbd-id": "129477",
        "x-bloks-version-id": "0e060251e1b0f688757fc85e86223bcf86d771ecddaa2fe9f1d86dabd2eda227",
        "x-csrftoken": "NLuYaoZXAfJWxcvB2xLl02DeraKbRWNv",
        "x-fb-friendly-name": "PolarisStoriesV3HighlightsPageQuery",
        "x-fb-lsd": "h1DiMkOiHePdJPBzWJzrKO",
        "x-ig-app-id": "936619743392459",
        "cookie": "ps_l=1; ps_n=1; mid=Zwkt3wALAAGiYlxgtUWozgf2unFE; datr=3i0JZ0Yom4G5r0sOVfvL8lFl; ig_did=42D71865-09BA-4449-8A17-7E98FF37A876; fbm_124024574287414=base_domain=.instagram.com; fbsr_124024574287414=IGPINZKyJzrNtoGUVjzM_c_CVvzrQz8-K93bBkGCWH8.eyJ1c2VyX2lkIjoiMTAwMDA4MDA0ODE0NzI1IiwiY29kZSI6IkFRQlNKRkQ5REE1WXpDNGoxYjVqQWFQeXdMd1JFTnFJUXdZNUduTG1qV2U5YzdPYUhWXzBmUEFVc1g4Y3FVSTZFZ0wwWTlWVEt3OVVTZmlyN1NKbFNkMWdUUXZfLTV0YzNhdjR3ZW15SWFfNnl2ZXZpb00tY1hZSTIyOUtzTFlXc3k0RUpYWDhtUnY0VEVNbjhRRFJYNURXdlMzLWlQelFFcG9xajVweUtFU3dPdnhib3VLWVdIMkhDajllNmtsWk1NSmFydUlzUDlLRGE0cHNEdGdZVlhvZl81QXRwYnZuVWNlTWtYa0ZySzhTaUdKY2FUZnYxQWtwUDFhN2tISHZ5MTN1SUJKMjc3NE9yQ0stU1lLa1k5ckc3SzFid3hxUEJnVEw0V0kzQ2hhbVZOSnhscUNPcVpORW40UVNrT185QzB6NE5VMmhNQUZ3T0NHVFBFaDZ0TjV4Iiwib2F1dGhfdG9rZW4iOiJFQUFCd3pMaXhuallCTzdxSGFMb1VoWDhLWkJmcHNKQ1RxbHBPSzhBUnFrZVRqWkF0VlpBbktXamREcmlWOUFTd1I4b1N3NHUzYlpBUHFzZUNySzRYYTd4enE1dlNFTzNBRWJaQW5MTmtFMmFWMENLeEx6bUZoSTM5WkFTZ1NxVG5tcGtlY3BPV2pHbDBoQkpUeHRlRVpCcmNPem5LMVd5dlJaQUpGOWI5bmNuYUtSbkJvNVdzVFpBdWZGMENzdXBiSkZGY250Z255MFpBdnZ2WkJsTmU3ZFpCb3BrWkQiLCJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTczMjIwODc3OX0; ds_user_id=8337021434; csrftoken=NLuYaoZXAfJWxcvB2xLl02DeraKbRWNv; wd=1366x334; sessionid=8337021434%3ABbbvIVauRVbjdS%3A9%3AAYdYucsv1Ny-slglxN7J0wol5BqDgVcXvxPLIoUyZg; rur=\"EAG\\0548337021434\\0541769443142:01f7ddfda67802e17fac2d284133e3e11498706a5757178daad6f75fb0c472cf1e6f131d\"",
        "Referer": "https://www.instagram.com/euronews_persian/",
        "Referrer-Policy": "strict-origin-when-cross-origin"
    },
        "body": "av=17841408323042127&__d=www&__user=0&__a=1&__req=2s&__hs=20114.HYP%3Ainstagram_web_pkg.2.1.0.0.1&dpr=1&__ccg=UNKNOWN&__rev=1019596031&__s=9e4k8m%3Ae0r3e5%3Adc4kax&__hsi=7464247596223464641&__dyn=7xeUjG1mxu1syUbFp41twpUnwgU7SbzEdF8aUco2qwJxS0k24o1DU2_CwjE1EE2Cw8G11wBz81s8hwGxu786a3a1YwBgao6C0Mo2iyo7u3ifK0EUjwGzEaE2iwNwmE2eUlwhEe87q0nKq2-azqwt8d-2u2J0bS1LwTwKG1pg2fwxyo6O1FwlEcUed6goK2O4Xxui2K7E5y4UrwHwGwa6bBK4o&__csr=gX5MV3cO6jNf2sAlbnsTIR8Bh17ldFbrcZ6ZpDgKvbAtGv9V9kECZVAirQUxGWiyFAhFaiiuUSmiiLGHxyjhaHQA9CyWAqRByV9rhe6pVammdAgS9mqjm5p4Ex3VER5HUiqAhV9m-ma_CyXiWy945UW4K-BGFpoW4Q5Bxum00jMK3-2Ewjwt19A8hk784R0JAg6G0gyhODxR1evK0D87y1FwkUe83Vw76wJw5yw1cG0K2ep218SdhA0Hyy8Za4-0C8n-10acwzjgmCF8IVi5wr86Cl2o9AmP385UMjwXG12wNwL4h85-5awkQ222fc1OgjBgog620BU9UdwwgEG7EfU0nIy8Cl00mZU0gywfG&__comet_req=7&fb_dtsg=NAcMrEnzFkEKp5rbYLTkpnaXiZRkzOgj1O9b3vQ0aP0W8ZqvBAsTf0w%3A17858449030071790%3A1737712569&jazoest=26391&lsd=h1DiMkOiHePdJPBzWJzrKO&__spin_r=1019596031&__spin_b=trunk&__spin_t=1737905571&fb_api_caller_class=RelayModern&fb_api_req_friendly_name=PolarisStoriesV3HighlightsPageQuery&variables=%7B%22initial_reel_id%22%3A%22highlight%3A18276957214204490%22%2C%22reel_ids%22%3A%5B%22highlight%3A18276957214204490%22%2C%22highlight%3A17930958344608052%22%2C%22highlight%3A17891630795726523%22%2C%22highlight%3A18185612626165637%22%5D%2C%22first%22%3A3%2C%22last%22%3A2%7D&server_timestamps=true&doc_id=29001692012763642",
        "method": "POST"
    });*/
}
