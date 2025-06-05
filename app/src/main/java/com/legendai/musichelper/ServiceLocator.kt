package com.legendai.musichelper

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

// Simple object providing shared dependencies without DI frameworks
object ServiceLocator {
    val httpClient: OkHttpClient by lazy {
        val pinner = CertificatePinner.Builder()
            .add(
                "api-inference.huggingface.co",
                "sha256/ivtlEFSWNlBFZKTeymRAlMziEv6bCcjNELEhKpI7mFQ="
            ).build()
        OkHttpClient.Builder()
            .certificatePinner(pinner)
            .build()
    }

    val repository: MusicRepository by lazy { MusicRepository(httpClient) }
}
