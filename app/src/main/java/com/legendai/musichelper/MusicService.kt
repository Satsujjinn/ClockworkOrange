package com.legendai.musichelper

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming

// Retrofit service calling the Hugging Face MusicGen model
interface MusicService {
    @Streaming
    @POST("models/facebook/musicgen-small")
    suspend fun generateSong(
        @Header("Authorization") apiKey: String,
        @Body request: GenerateSongRequest
    ): ResponseBody
}
