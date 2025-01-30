package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl

fun main() {
    val api = Api()
    api.loadCookies()

    val res = api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        Api.GraphQlQuery.LIKE_POST.body(
            "3556229450513837363",
            "GCA3NDFjNDQ5MTQzYmI0N2NkODNmNmZhMGQ5OWQ4MmUwOUaYrtj5DCaYrtj5DAA="
        )
    )
    println(res.data?.xdt_api__v1__media__media_id__like?.__typename)

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
        "x-bloks-version-id": "147ce6467d90873c56559b58f768f8010c9a176fd3f10fb32fbf5518945ae49e",
        "x-csrftoken": "NLuYaoZXAfJWxcvB2xLl02DeraKbRWNv",
        "x-fb-friendly-name": "usePolarisLikeMediaLikeMutation",
        "x-fb-lsd": "nfsuaMcIrSgOVlQCWoheV_",
        "x-ig-app-id": "936619743392459",
        "cookie": "ps_l=1; ps_n=1; mid=Zwkt3wALAAGiYlxgtUWozgf2unFE; datr=3i0JZ0Yom4G5r0sOVfvL8lFl; ig_did=42D71865-09BA-4449-8A17-7E98FF37A876; fbm_124024574287414=base_domain=.instagram.com; fbsr_124024574287414=IGPINZKyJzrNtoGUVjzM_c_CVvzrQz8-K93bBkGCWH8.eyJ1c2VyX2lkIjoiMTAwMDA4MDA0ODE0NzI1IiwiY29kZSI6IkFRQlNKRkQ5REE1WXpDNGoxYjVqQWFQeXdMd1JFTnFJUXdZNUduTG1qV2U5YzdPYUhWXzBmUEFVc1g4Y3FVSTZFZ0wwWTlWVEt3OVVTZmlyN1NKbFNkMWdUUXZfLTV0YzNhdjR3ZW15SWFfNnl2ZXZpb00tY1hZSTIyOUtzTFlXc3k0RUpYWDhtUnY0VEVNbjhRRFJYNURXdlMzLWlQelFFcG9xajVweUtFU3dPdnhib3VLWVdIMkhDajllNmtsWk1NSmFydUlzUDlLRGE0cHNEdGdZVlhvZl81QXRwYnZuVWNlTWtYa0ZySzhTaUdKY2FUZnYxQWtwUDFhN2tISHZ5MTN1SUJKMjc3NE9yQ0stU1lLa1k5ckc3SzFid3hxUEJnVEw0V0kzQ2hhbVZOSnhscUNPcVpORW40UVNrT185QzB6NE5VMmhNQUZ3T0NHVFBFaDZ0TjV4Iiwib2F1dGhfdG9rZW4iOiJFQUFCd3pMaXhuallCTzdxSGFMb1VoWDhLWkJmcHNKQ1RxbHBPSzhBUnFrZVRqWkF0VlpBbktXamREcmlWOUFTd1I4b1N3NHUzYlpBUHFzZUNySzRYYTd4enE1dlNFTzNBRWJaQW5MTmtFMmFWMENLeEx6bUZoSTM5WkFTZ1NxVG5tcGtlY3BPV2pHbDBoQkpUeHRlRVpCcmNPem5LMVd5dlJaQUpGOWI5bmNuYUtSbkJvNVdzVFpBdWZGMENzdXBiSkZGY250Z255MFpBdnZ2WkJsTmU3ZFpCb3BrWkQiLCJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTczMjIwODc3OX0; ds_user_id=8337021434; csrftoken=NLuYaoZXAfJWxcvB2xLl02DeraKbRWNv; sessionid=8337021434%3ABbbvIVauRVbjdS%3A9%3AAYffmlmQfkKb3pk_4pF-NH2bi4Z6XFyFvWtXicIpgWY; wd=1366x334; rur=\"NHA\\0548337021434\\0541769750426:01f7be55cb9ba968c734d53e08e39ff0f82ac8fa4e7af015f4abcd059a5af886e9d2512b\"",
        "Referer": "https://www.instagram.com/",
        "Referrer-Policy": "strict-origin-when-cross-origin"
    },
        "body": "av=17841408323042127&__d=www&__user=0&__a=1&__req=17&__hs=20118.HYP%3Ainstagram_web_pkg.2.1...1&dpr=1&__ccg=EXCELLENT&__rev=1019687290&__s=dqzyuh%3Aed2u07%3Awjo7e7&__hsi=7465573500953098942&__dyn=7xeUjG1mxu1syUbFp41twpUnwgU7SbzEdF8aUco2qwJxS0k24o0B-q1ew6ywaq0yE462mcw5Mx62G5UswoEcE7O2l0Fwqo31w9a9wtUd8-U2zxe2GewGw9a361qw8Xxm16wUwtE1uVEbUGdG1QwTU9UaQ0Lo6-3u2WE5B08-269wr86C1mwPwUQp1yUb8jK5V89F8uwm8jxK2K2G0EoKmUhw&__csr=gT1L2QQ9hsbhcJaghsWOqTiQJOq8BQRYHl9pliDWKAOp6AJ7gCjB8vXAhA_WR-9CK8gyFFBTKrp5DByeFRAG-HvgS_FAjxeeGqpA87EXzBBUjyrymlpF4dAhpXxGu8QdDyUyVbhUOimvWGq8iyKfUkDy9efLHxi8gjujcEkw04Xuyiw9uag-294gvGje3O4plw4ZwLBl1JwFWwnE3lwJxC1kwfW0ri0fsw2to3NQ9g3Dl28xqwJx29Iw5O3Om3-daEMMj4Ow9Kl0jEqhpQ9QA35Q3p0GK12wLwbWElwwomwDg4wE1lQ2J5CgC30h4x11890KwLw1gYE05YK0fiw47w&__comet_req=7&fb_dtsg=NAcPW7lTnyMLltP_GoBH2qz4ghkDnMyVG_rJsmZv4wzCcN7LB3_FTaQ%3A17858449030071790%3A1737712569&jazoest=26403&lsd=nfsuaMcIrSgOVlQCWoheV_&__spin_r=1019687290&__spin_b=trunk&__spin_t=1738214283&fb_api_caller_class=RelayModern&fb_api_req_friendly_name=usePolarisLikeMediaLikeMutation&variables=%7B%22media_id%22%3A%223556229450513837363%22%2C%22container_module%22%3A%22feed_timeline%22%2C%22inventory_source%22%3A%22media_or_ad%22%2C%22ranking_info_token%22%3A%22GCA3NDFjNDQ5MTQzYmI0N2NkODNmNmZhMGQ5OWQ4MmUwOUaYrtj5DCaYrtj5DAA%3D%22%2C%22nav_chain%22%3A%22PolarisFeedRoot%3AfeedPage%3A1%3Avia_cold_start%22%7D&server_timestamps=true&doc_id=8552604541488484",
        "method": "POST"
    });*/
}
