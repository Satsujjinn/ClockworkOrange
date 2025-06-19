package com.clockworkred.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/** Interceptor that adds an API key header to requests. */
class ApiKeyInterceptor(private val apiKeyProvider: () -> String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            // TODO replace with real header key
            .addHeader("Authorization", apiKeyProvider())
            .build()
        return chain.proceed(request)
    }
}
