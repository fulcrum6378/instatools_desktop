package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl
import java.net.URLEncoder
import java.util.Calendar

@Suppress("SpellCheckingInspection")
fun main() {
    val api = Api()
    val variables = "%7B%22data%22%3A%7B%22count%22%3A12%2C%22include_reel_media_seen_timestamp%22%3Atrue%2C%22include_relationship_info%22%3Atrue%2C%22latest_besties_reel_media%22%3Atrue%2C%22latest_reel_media%22%3Atrue%7D%2C%22username%22%3A%22toni.marie.graham%22%2C%22__relay_internal__pv__PolarisIsLoggedInrelayprovider%22%3Atrue%7D"
        /*"{" +
                "\"data\":" +
                "{" +
                "\"count\":12," +
                "\"include_reel_media_seen_timestamp\":true," +
                "\"include_relationship_info\":true," +
                "\"latest_besties_reel_media\":true," +
                "\"latest_reel_media\":true" +
                "}," +
                "\"username\":\"toni.marie.graham\"," +
                "\"__relay_internal__pv__PolarisIsLoggedInrelayprovider\":true" +
                "}"*/
    val pBody = "av=17841408323042127&__d=www&__user=0&__a=1&__req=7&__hs=20112.HYP%3Ainstagram_web_pkg.2.1.0.0.1&dpr=1&__ccg=UNKNOWN&__rev=1019563935&__s=yst1x5%3Akxk10f%3Aih8vka&__hsi=7463585458754054578&__dyn=7xeUjG1mxu1syUbFp41twpUnwgU7SbzEdF8aUco2qwJxS0k24o1DU2_CwjE1EE2Cw8G11wBz81s8hwGxu786a3a1YwBgao6C0Mo2swtUd8-U2zxe2GewGw9a361qw8Xxm16wUwtE1uVEbUGdG1QwTU9UaQ0Lo6-3u2WE5B08-269wr86C1mwPwUQp1yUb8jK5V8aUuwm8jxK2K0P8KmUhw&__csr=gZb13czNtYmBP8sLtv9vR8j8JFi-pdueQruGBHVaBWg-HEyQWAuA9mi9FDBAydqhKQhWyAaXoOlrQRV8Kaz8xpUCeQEKuuEFaha8p9p8mzGyFpUiAmA9prJa9jKVFd1ycDzFp8CmGzQ8KnG5mHye5oizKl4zFpQGgy5Q8w04Lqw4MO040Cwfx0zBc5o4t0iK4i0eyUi40ci1VDyo9bG58kwnS9wVw3Oo8U0TS09wwkQt0boyx2582DF4wLm8ChpB209oZ1eSQtd0n84v46K4C0ZcM8awlohwYS1wxyE4V0zChpA9Cws40iG4IEchp5205611w1s64ojQ01scw2080liw&__comet_req=7&fb_dtsg=NAcOtXqW1L9ddG5C-8eMAYOVTtpHjljkwJcMhTSzMwE_4rYlBnizRsg%3A17858449030071790%3A1737712569&jazoest=26428&lsd=gNhey-t_W3RnfCYH5LPDDV&__spin_r=1019563935&__spin_b=trunk&__spin_t=1737751406&fb_api_caller_class=RelayModern&fb_api_req_friendly_name=PolarisProfilePostsQuery&variables=%7B%22data%22%3A%7B%22count%22%3A12%2C%22include_reel_media_seen_timestamp%22%3Atrue%2C%22include_relationship_info%22%3Atrue%2C%22latest_besties_reel_media%22%3Atrue%2C%22latest_reel_media%22%3Atrue%7D%2C%22username%22%3A%22toni.marie.graham%22%2C%22__relay_internal__pv__PolarisIsLoggedInrelayprovider%22%3Atrue%7D&server_timestamps=true&doc_id=8934560356598281"
        /*"av=17841408323042127" +
            "&__d=www" +
            "&__user=0" +
            "&__a=1" +
            "&__req=7" +
            "&__hs=20112.HYP%3Ainstagram_web_pkg.2.1.0.0.1" +
            "&dpr=1" +
            "&__ccg=UNKNOWN" +
            "&__rev=1019563935" +
            "&__s=yst1x5%3Akxk10f%3Aih8vka" +
            "&__hsi=7463585458754054578" +
            "&__dyn=7xeUjG1mxu1syUbFp41twpUnwgU7SbzEdF8aUco2qwJxS0k24o1DU2_CwjE1EE2Cw8G11wBz81s8hwGxu786a3a1YwBgao6C0Mo2swtUd8-U2zxe2GewGw9a361qw8Xxm16wUwtE1uVEbUGdG1QwTU9UaQ0Lo6-3u2WE5B08-269wr86C1mwPwUQp1yUb8jK5V8aUuwm8jxK2K0P8KmUhw" +
            "&__csr=gZb13czNtYmBP8sLtv9vR8j8JFi-pdueQruGBHVaBWg-HEyQWAuA9mi9FDBAydqhKQhWyAaXoOlrQRV8Kaz8xpUCeQEKuuEFaha8p9p8mzGyFpUiAmA9prJa9jKVFd1ycDzFp8CmGzQ8KnG5mHye5oizKl4zFpQGgy5Q8w04Lqw4MO040Cwfx0zBc5o4t0iK4i0eyUi40ci1VDyo9bG58kwnS9wVw3Oo8U0TS09wwkQt0boyx2582DF4wLm8ChpB209oZ1eSQtd0n84v46K4C0ZcM8awlohwYS1wxyE4V0zChpA9Cws40iG4IEchp5205611w1s64ojQ01scw2080liw" +
            "&__comet_req=7" +
            "&fb_dtsg=NAcOtXqW1L9ddG5C-8eMAYOVTtpHjljkwJcMhTSzMwE_4rYlBnizRsg%3A17858449030071790%3A1737712569" +
            "&jazoest=26428" +
            "&lsd=gNhey-t_W3RnfCYH5LPDDV" +
            "&__spin_r=1019563935" +
            "&__spin_b=trunk" +
            "&__spin_t=" + (Calendar.getInstance().timeInMillis / 1000).toString() +  //1737751406
            "&fb_api_caller_class=RelayModern" +
            "&fb_api_req_friendly_name=PolarisProfilePostsQuery" +
            "&variables=$variables" +
            //"&variables=${URLEncoder.encode(variables,"utf-8")}" +
            "&server_timestamps=true" +
            "&doc_id=8934560356598281"*/
    api.call<GraphQl>(Api.Endpoint.QUERY.url, GraphQl::class, true, pBody)
        .data?.xdt_api__v1__media__shortcode__web_info


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
        "x-fb-friendly-name": "PolarisProfilePostsQuery",
        "x-fb-lsd": "gNhey-t_W3RnfCYH5LPDDV",
        "x-ig-app-id": "936619743392459",
        "cookie": "ps_l=1; ps_n=1; mid=Zwkt3wALAAGiYlxgtUWozgf2unFE; datr=3i0JZ0Yom4G5r0sOVfvL8lFl; ig_did=42D71865-09BA-4449-8A17-7E98FF37A876; fbm_124024574287414=base_domain=.instagram.com; fbsr_124024574287414=IGPINZKyJzrNtoGUVjzM_c_CVvzrQz8-K93bBkGCWH8.eyJ1c2VyX2lkIjoiMTAwMDA4MDA0ODE0NzI1IiwiY29kZSI6IkFRQlNKRkQ5REE1WXpDNGoxYjVqQWFQeXdMd1JFTnFJUXdZNUduTG1qV2U5YzdPYUhWXzBmUEFVc1g4Y3FVSTZFZ0wwWTlWVEt3OVVTZmlyN1NKbFNkMWdUUXZfLTV0YzNhdjR3ZW15SWFfNnl2ZXZpb00tY1hZSTIyOUtzTFlXc3k0RUpYWDhtUnY0VEVNbjhRRFJYNURXdlMzLWlQelFFcG9xajVweUtFU3dPdnhib3VLWVdIMkhDajllNmtsWk1NSmFydUlzUDlLRGE0cHNEdGdZVlhvZl81QXRwYnZuVWNlTWtYa0ZySzhTaUdKY2FUZnYxQWtwUDFhN2tISHZ5MTN1SUJKMjc3NE9yQ0stU1lLa1k5ckc3SzFid3hxUEJnVEw0V0kzQ2hhbVZOSnhscUNPcVpORW40UVNrT185QzB6NE5VMmhNQUZ3T0NHVFBFaDZ0TjV4Iiwib2F1dGhfdG9rZW4iOiJFQUFCd3pMaXhuallCTzdxSGFMb1VoWDhLWkJmcHNKQ1RxbHBPSzhBUnFrZVRqWkF0VlpBbktXamREcmlWOUFTd1I4b1N3NHUzYlpBUHFzZUNySzRYYTd4enE1dlNFTzNBRWJaQW5MTmtFMmFWMENLeEx6bUZoSTM5WkFTZ1NxVG5tcGtlY3BPV2pHbDBoQkpUeHRlRVpCcmNPem5LMVd5dlJaQUpGOWI5bmNuYUtSbkJvNVdzVFpBdWZGMENzdXBiSkZGY250Z255MFpBdnZ2WkJsTmU3ZFpCb3BrWkQiLCJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTczMjIwODc3OX0; ds_user_id=8337021434; csrftoken=NLuYaoZXAfJWxcvB2xLl02DeraKbRWNv; sessionid=8337021434%3ABbbvIVauRVbjdS%3A9%3AAYdCKYWyYf_HHfbMJCIswmHVqbXp2dhIFa3wo6WwLA; rur=\"CCO\\0548337021434\\0541769281718:01f74a45a0633a20831369ca7c5756bcce7a7850969c4c5af643ee8fd5261424c9afae9a\"; wd=1366x362",
        "Referer": "https://www.instagram.com/toni.marie.graham",
        "Referrer-Policy": "strict-origin-when-cross-origin"
    },
        "body": "av=17841408323042127&__d=www&__user=0&__a=1&__req=7&__hs=20112.HYP%3Ainstagram_web_pkg.2.1.0.0.1&dpr=1&__ccg=UNKNOWN&__rev=1019563935&__s=yst1x5%3Akxk10f%3Aih8vka&__hsi=7463585458754054578&__dyn=7xeUjG1mxu1syUbFp41twpUnwgU7SbzEdF8aUco2qwJxS0k24o1DU2_CwjE1EE2Cw8G11wBz81s8hwGxu786a3a1YwBgao6C0Mo2swtUd8-U2zxe2GewGw9a361qw8Xxm16wUwtE1uVEbUGdG1QwTU9UaQ0Lo6-3u2WE5B08-269wr86C1mwPwUQp1yUb8jK5V8aUuwm8jxK2K0P8KmUhw&__csr=gZb13czNtYmBP8sLtv9vR8j8JFi-pdueQruGBHVaBWg-HEyQWAuA9mi9FDBAydqhKQhWyAaXoOlrQRV8Kaz8xpUCeQEKuuEFaha8p9p8mzGyFpUiAmA9prJa9jKVFd1ycDzFp8CmGzQ8KnG5mHye5oizKl4zFpQGgy5Q8w04Lqw4MO040Cwfx0zBc5o4t0iK4i0eyUi40ci1VDyo9bG58kwnS9wVw3Oo8U0TS09wwkQt0boyx2582DF4wLm8ChpB209oZ1eSQtd0n84v46K4C0ZcM8awlohwYS1wxyE4V0zChpA9Cws40iG4IEchp5205611w1s64ojQ01scw2080liw&__comet_req=7&fb_dtsg=NAcOtXqW1L9ddG5C-8eMAYOVTtpHjljkwJcMhTSzMwE_4rYlBnizRsg%3A17858449030071790%3A1737712569&jazoest=26428&lsd=gNhey-t_W3RnfCYH5LPDDV&__spin_r=1019563935&__spin_b=trunk&__spin_t=1737751406&fb_api_caller_class=RelayModern&fb_api_req_friendly_name=PolarisProfilePostsQuery&variables=%7B%22data%22%3A%7B%22count%22%3A12%2C%22include_reel_media_seen_timestamp%22%3Atrue%2C%22include_relationship_info%22%3Atrue%2C%22latest_besties_reel_media%22%3Atrue%2C%22latest_reel_media%22%3Atrue%7D%2C%22username%22%3A%22toni.marie.graham%22%2C%22__relay_internal__pv__PolarisIsLoggedInrelayprovider%22%3Atrue%7D&server_timestamps=true&doc_id=8934560356598281",
        "method": "POST"
    });*/
}

/*fun encode(uriString: String?): String? {
    if (uriString == null) return null
    if (TextUtils.isEmpty(uriString)) return uriString
    val allowedUrlCharacters = Pattern.compile(
        "([A-Za-z\\d_.~:/?#\\[\\]@!$&'()*+,;" + "=-]|%[\\da-fA-F]{2})+"
    )
    val matcher = allowedUrlCharacters.matcher(uriString)
    var validUri: String? = null
    if (matcher.find()) validUri = matcher.group()
    if (TextUtils.isEmpty(validUri) || uriString.length == validUri!!.length)
        return uriString

    val uri = Uri.parse(uriString)
    val uriBuilder = Uri.Builder().scheme(uri.scheme).authority(uri.authority)
    for (path in uri.pathSegments) uriBuilder.appendPath(path)
    for (key in uri.queryParameterNames)
        uriBuilder.appendQueryParameter(key, uri.getQueryParameter(key))
    return uriBuilder.build().toString()
}*/
