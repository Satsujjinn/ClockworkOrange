package com.legendai.musichelper

// Repository executing Retrofit calls
class MusicRepository(private val service: MusicService) {

    suspend fun generateSong(
        apiKey: String,
        request: GenerateSongRequest,
        context: android.content.Context
    ): GenerateSongResponse {
        val body = service.generateSong("Bearer $apiKey", request)
        val file = java.io.File.createTempFile("musicgen_", ".wav", context.cacheDir)
        file.outputStream().use { out -> out.write(body.bytes()) }
        return GenerateSongResponse(file.absolutePath)
    }

}
