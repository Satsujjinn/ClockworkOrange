package com.clockworkred.data

import com.clockworkred.domain.repository.SettingsRepository
import com.clockworkred.domain.AiRepository
import com.clockworkred.data.remote.AiService
import com.clockworkred.data.remote.ApiKeyInterceptor
import com.clockworkred.data.repository.AiRepositoryImpl
import com.clockworkred.data.BuildConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAiRepository(impl: AiRepositoryImpl): AiRepository

    companion object {
        @Provides
        @Singleton
        fun provideOkHttpClient(settings: SettingsRepository): OkHttpClient {
            val apiKey = runBlocking { settings.getApiKey().first() } ?: ""
            return OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor { apiKey })
                .build()
        }

        @Provides
        @Singleton
        fun provideAiService(client: OkHttpClient): AiService {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(AiService::class.java)
        }
    }
}
