package com.legendai.musichelper

import android.content.Context
import java.io.File
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// Repository executing network calls with OkHttp
class MusicRepository(private val client: OkHttpClient) {

    suspend fun generateSong(
        apiKey: String,
        request: GenerateSongRequest,
        context: Context
    ): GenerateSongResponse {
        val json = Json.encodeToString(request)
        val body = json.toRequestBody("application/json".toMediaType())
        val httpRequest = Request.Builder()
            .url(Config.API_BASE_URL + "models/facebook/musicgen-small")
            .post(body)
            .addHeader("Authorization", "Bearer $apiKey")
            .build()
        client.newCall(httpRequest).execute().use { response ->
            if (!response.isSuccessful) throw java.io.IOException("Network error")
            val file = File.createTempFile("musicgen_", ".wav", context.cacheDir)
            response.body?.byteStream()?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            return GenerateSongResponse(file.absolutePath)
        }
    }
}
