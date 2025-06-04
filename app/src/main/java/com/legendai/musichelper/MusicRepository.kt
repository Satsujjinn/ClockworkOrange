package com.legendai.musichelper

// Repository executing Retrofit calls
class MusicRepository(private val service: MusicService) {

    suspend fun generateSong(apiKey: String, request: GenerateSongRequest): GenerateSongResponse {
        return service.generateSong("Bearer $apiKey", request)
    }

}
