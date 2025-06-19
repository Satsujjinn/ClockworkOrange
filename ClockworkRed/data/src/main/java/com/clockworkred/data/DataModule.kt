package com.clockworkred.data

import com.clockworkred.domain.ProjectRepository
import com.clockworkred.domain.SettingsRepository
import com.clockworkred.domain.AiRepository
import com.clockworkred.data.remote.AiService
import com.clockworkred.data.remote.ApiKeyInterceptor
import com.clockworkred.data.repository.AiRepositoryImpl
import com.clockworkred.data.repository.FakeProjectRepository
import com.clockworkred.data.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindProjectRepository(impl: FakeProjectRepository): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(impl: AiRepositoryImpl): AiRepository

    companion object {
        @Provides
        @Singleton
        fun provideOkHttpClient(settings: SettingsRepository): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor { 
                    // TODO read API key from SettingsRepository synchronously
                    "" 
                })
                .build()
        }

        @Provides
        @Singleton
        fun provideAiService(client: OkHttpClient): AiService {
            return Retrofit.Builder()
                // TODO replace with real base URL
                .baseUrl("https://example.com")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(AiService::class.java)
        }
    }
}
