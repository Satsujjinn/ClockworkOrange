package com.legendai.musichelper

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Retrofit service defining LegendAI API endpoints
interface MusicService {
    @POST("generateSong")
    suspend fun generateSong(
        @Header("Authorization") apiKey: String,
        @Body request: GenerateSongRequest
    ): GenerateSongResponse
}
