package com.legendai.musichelper

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import okhttp3.MediaType.Companion.toMediaType

// Repository executing Retrofit calls
class MusicRepository(private val service: MusicService) {

    suspend fun generateSong(apiKey: String, request: GenerateSongRequest): GenerateSongResponse {
        return service.generateSong("Bearer $apiKey", request)
    }

    companion object {
        fun create(): MusicRepository {
            val client = OkHttpClient.Builder().build()
            val json = Json { ignoreUnknownKeys = true }
            val retrofit = Retrofit.Builder()
                .baseUrl(Config.API_BASE_URL)
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
            val service = retrofit.create(MusicService::class.java)
            return MusicRepository(service)
        }
    }
}
