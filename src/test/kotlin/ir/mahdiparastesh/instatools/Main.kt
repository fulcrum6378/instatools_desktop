package ir.mahdiparastesh.instatools

import ir.mahdiparastesh.instatools.api.Api
import ir.mahdiparastesh.instatools.api.GraphQl

fun main() {
    val api = Api()
    api.loadCookies()

    val cook = "ps_l=1; ps_n=1; mid=Zwkt3wALAAGiYlxgtUWozgf2unFE; datr=3i0JZ0Yom4G5r0sOVfvL8lFl; ig_did=42D71865-09BA-4449-8A17-7E98FF37A876; fbm_124024574287414=base_domain=.instagram.com; fbsr_124024574287414=IGPINZKyJzrNtoGUVjzM_c_CVvzrQz8-K93bBkGCWH8.eyJ1c2VyX2lkIjoiMTAwMDA4MDA0ODE0NzI1IiwiY29kZSI6IkFRQlNKRkQ5REE1WXpDNGoxYjVqQWFQeXdMd1JFTnFJUXdZNUduTG1qV2U5YzdPYUhWXzBmUEFVc1g4Y3FVSTZFZ0wwWTlWVEt3OVVTZmlyN1NKbFNkMWdUUXZfLTV0YzNhdjR3ZW15SWFfNnl2ZXZpb00tY1hZSTIyOUtzTFlXc3k0RUpYWDhtUnY0VEVNbjhRRFJYNURXdlMzLWlQelFFcG9xajVweUtFU3dPdnhib3VLWVdIMkhDajllNmtsWk1NSmFydUlzUDlLRGE0cHNEdGdZVlhvZl81QXRwYnZuVWNlTWtYa0ZySzhTaUdKY2FUZnYxQWtwUDFhN2tISHZ5MTN1SUJKMjc3NE9yQ0stU1lLa1k5ckc3SzFid3hxUEJnVEw0V0kzQ2hhbVZOSnhscUNPcVpORW40UVNrT185QzB6NE5VMmhNQUZ3T0NHVFBFaDZ0TjV4Iiwib2F1dGhfdG9rZW4iOiJFQUFCd3pMaXhuallCTzdxSGFMb1VoWDhLWkJmcHNKQ1RxbHBPSzhBUnFrZVRqWkF0VlpBbktXamREcmlWOUFTd1I4b1N3NHUzYlpBUHFzZUNySzRYYTd4enE1dlNFTzNBRWJaQW5MTmtFMmFWMENLeEx6bUZoSTM5WkFTZ1NxVG5tcGtlY3BPV2pHbDBoQkpUeHRlRVpCcmNPem5LMVd5dlJaQUpGOWI5bmNuYUtSbkJvNVdzVFpBdWZGMENzdXBiSkZGY250Z255MFpBdnZ2WkJsTmU3ZFpCb3BrWkQiLCJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTczMjIwODc3OX0; ds_user_id=8337021434; csrftoken=NLuYaoZXAfJWxcvB2xLl02DeraKbRWNv; wd=1366x334; sessionid=8337021434%3ABbbvIVauRVbjdS%3A9%3AAYdYucsv1Ny-slglxN7J0wol5BqDgVcXvxPLIoUyZg; rur=\\\"EAG\\\\0548337021434\\\\0541769443142:01f7ddfda67802e17fac2d284133e3e11498706a5757178daad6f75fb0c472cf1e6f131d\\\""
    println(api.cookies == cook) // false
    println(api.cookies)

    api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        Api.GraphQlQuery.STORY.body("\"2003616263\"")
    ).data?.xdt_api__v1__feed__reels_media?.reels_media?.forEach { userStory ->
        userStory.items!!.forEachIndexed { i, story ->
            println("$i. " + story.link())
        }
    }
    println("\n\n")

    val hlIds = api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        Api.GraphQlQuery.PROFILE_HIGHLIGHTS_TRAY.body("2003616263")
    ).data?.highlights?.edges?.map { "\"${it.node.id}\"" }
    if (hlIds == null) throw Api.FailureException(-3)

    if (hlIds.isEmpty())
        println("No highlighted stories.")
    else api.call<GraphQl>(
        Api.Endpoint.QUERY.url, GraphQl::class, true,
        Api.GraphQlQuery.HIGHLIGHTS.body(hlIds.joinToString(","), hlIds[0])
    ).data?.xdt_api__v1__feed__reels_media__connection?.also { page ->
        page.edges.forEach { tray ->
            tray.node.items!!.forEachIndexed { i, story ->
                println("${tray.node.id} : $i. " + story.link())
            }
        }
    } ?: throw Api.FailureException(-3)
}
