package com.clockworkred.app

import com.clockworkred.data.repository.FakeArrangementRepository
import com.clockworkred.data.repository.FakeProjectRepository
import com.clockworkred.data.repository.SettingsRepositoryImpl
import com.clockworkred.domain.ArrangementRepository
import com.clockworkred.domain.ProjectRepository
import com.clockworkred.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Hilt bindings for app-level dependencies. */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindArrangementRepository(impl: FakeArrangementRepository): ArrangementRepository

    @Binds
    @Singleton
    abstract fun bindProjectRepository(impl: FakeProjectRepository): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
