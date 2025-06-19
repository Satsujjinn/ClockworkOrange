package com.clockworkred.data.remote

import com.clockworkred.data.remote.model.AiTabResultDto
import com.clockworkred.data.remote.model.PartRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

/** Retrofit service for AI tab generation. */
interface AiService {
    @POST("/generate-tab")
    suspend fun generateTab(@Body request: PartRequestDto): AiTabResultDto
}
