package com.clockworkred.data

import com.clockworkred.domain.ProjectRepository
import com.clockworkred.domain.SettingsRepository
import com.clockworkred.data.repository.FakeProjectRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
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

    companion object {
        @Provides
        @Singleton
        fun provideAiService(): AiService {
            return Retrofit.Builder()
                .baseUrl("https://example.com")
                .build()
                .create(AiService::class.java)
        }
    }
}
