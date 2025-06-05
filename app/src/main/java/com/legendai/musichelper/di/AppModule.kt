package com.legendai.musichelper.di

import com.legendai.musichelper.MusicRepository
import com.legendai.musichelper.MusicService
import com.legendai.musichelper.Config
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.CertificatePinner
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val pinner = CertificatePinner.Builder()
            .add(
                "api-inference.huggingface.co",
                "sha256/ivtlEFSWNlBFZKTeymRAlMziEv6bCcjNELEhKpI7mFQ="
            ).build()
        return OkHttpClient.Builder()
            .certificatePinner(pinner)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(Config.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideMusicService(retrofit: Retrofit): MusicService =
        retrofit.create(MusicService::class.java)

    @Provides
    @Singleton
    fun provideMusicRepository(service: MusicService): MusicRepository =
        MusicRepository(service)
}
