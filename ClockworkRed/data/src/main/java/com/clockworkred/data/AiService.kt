package com.clockworkred.data

import retrofit2.http.GET

interface AiService {
    @GET("/todo")
    suspend fun placeholder()
}
