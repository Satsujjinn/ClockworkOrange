package com.legendai.musichelper.spotify

import com.legendai.musichelper.Config
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URLEncoder

class SpotifyService(
    private val client: OkHttpClient,
    private val baseUrl: String = Config.SPOTIFY_BASE_URL
) {
    private val json = Json { ignoreUnknownKeys = true }

    @Throws(IOException::class)
    fun searchAndAnalyze(apiKey: String, query: String): SpotifyTrack? {
        val searchUrl = baseUrl + "search?q=" +
            URLEncoder.encode(query, "UTF-8") + "&type=track&limit=1"
        val searchReq = Request.Builder()
            .url(searchUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .build()
        val track = client.newCall(searchReq).execute().use { resp ->
            if (!resp.isSuccessful) throw IOException("Search failed")
            val root = json.parseToJsonElement(resp.body!!.string()).jsonObject
            val item = root["tracks"]?.jsonObject?.get("items")?.jsonArray
                ?.firstOrNull()?.jsonObject ?: return null
            val id = item["id"]!!.jsonPrimitive.content
            val name = item["name"]!!.jsonPrimitive.content
            val artist = item["artists"]!!.jsonArray
                .first().jsonObject["name"]!!.jsonPrimitive.content
            SpotifyTrack(id = id, name = name, artist = artist)
        } ?: return null

        val featuresUrl = baseUrl + "audio-features/" + track.id
        val featuresReq = Request.Builder()
            .url(featuresUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .build()
        return client.newCall(featuresReq).execute().use { resp ->
            if (!resp.isSuccessful) throw IOException("Features failed")
            val root = json.parseToJsonElement(resp.body!!.string()).jsonObject
            track.copy(
                tempo = root["tempo"]?.jsonPrimitive?.float,
                key = root["key"]?.jsonPrimitive?.int,
                mode = root["mode"]?.jsonPrimitive?.int
            )
        }
    }
}
