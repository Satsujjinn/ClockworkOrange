package com.clockworkred.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/** Interceptor that adds an API key header to requests. */
class ApiKeyInterceptor(private val apiKeyProvider: () -> String) : Interceptor {
    companion object {
        const val HEADER = "X-Api-Key"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader(HEADER, apiKeyProvider())
            .build()
        return chain.proceed(request)
    }
}
