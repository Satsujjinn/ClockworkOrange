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
import com.legendai.musichelper.network.ProgressResponseBody

// Repository executing network calls with OkHttp
import okhttp3.Call

class MusicRepository(private val client: OkHttpClient) {

    private var activeCall: Call? = null

    fun cancel() {
        activeCall?.cancel()
        activeCall = null
    }

    suspend fun generateSong(
        apiKey: String,
        request: GenerateSongRequest,
        context: Context,
        onProgress: (Float) -> Unit = {}
    ): GenerateSongResponse {
        val json = Json.encodeToString(request)
        val body = json.toRequestBody("application/json".toMediaType())
        val httpRequest = Request.Builder()
            .url(Config.API_BASE_URL + "models/facebook/musicgen-small")
            .post(body)
            .addHeader("Authorization", "Bearer $apiKey")
            .build()
        val call = client.newCall(httpRequest)
        activeCall = call
        try {
            call.execute().use { response ->
                if (!response.isSuccessful) throw java.io.IOException("Network error")

                val body = response.body ?: throw java.io.IOException("Empty body")
                val progressBody = ProgressResponseBody(body, onProgress)

                val file = File.createTempFile("musicgen_", ".wav", context.cacheDir)
                progressBody.byteStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                return GenerateSongResponse(file.absolutePath)
            }
        } finally {
            activeCall = null
        }
    }
}
