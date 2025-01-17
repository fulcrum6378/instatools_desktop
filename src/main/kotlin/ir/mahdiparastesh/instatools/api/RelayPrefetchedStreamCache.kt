package ir.mahdiparastesh.instatools.api

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

object RelayPrefetchedStreamCache {
    private const val HTML_TAG_SCRIPT_JSON = "<script type=\"application/json\""
    private val gsonType = object : TypeToken<Map<String, List<List<Any>>>>() {}.type

    @Suppress("UNCHECKED_CAST")
    fun crawl(html: String): HashMap<String, Map<String, Any>> {
        var read = html
        var json: String
        var gson: Map<String, List<List<Any>>>
        var tuple: List<Any>
        val data = hashMapOf<String, Map<String, Any>>()
        while (read.contains(HTML_TAG_SCRIPT_JSON)) {
            read = read.substringAfter(HTML_TAG_SCRIPT_JSON).substringAfter(">")
            json = read.substringBefore("</script>")
            if (json.contains("RelayPrefetchedStreamCache")) {
                gson = (GsonBuilder().setLenient().create().fromJson(json, gsonType))
                gson = (gson["require"]!![0][3] as List<Map<String, Any?>>)[0]["__bbox"]!!
                        as Map<String, List<List<Any>>>
                tuple = gson["require"]!![0][3] as List<Any>
                data[(tuple[0] as String).split("_")[1]] =
                    (((tuple[1] as Map<String, Map<String, Any>>)["__bbox"]!!["result"]
                            as Map<String, Map<String, Any>>)["data"])!!.values.first() as Map<String, Any>
            }
            read = read.substringAfter("</script>")
        }
        return data
    }
}
