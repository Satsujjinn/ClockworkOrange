package com.clockworkred.data

import com.clockworkred.domain.ProjectRepository
import com.clockworkred.domain.SettingsRepository
import com.clockworkred.domain.AiRepository
import com.clockworkred.data.remote.AiService
import com.clockworkred.data.repository.AiRepositoryImpl
import com.clockworkred.data.repository.FakeProjectRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
        fun provideAiService(): AiService {
            return Retrofit.Builder()
                // TODO replace with real base URL
                .baseUrl("https://example.com")
                // TODO inject authentication interceptor for API key
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AiService::class.java)
        }
    }
}
