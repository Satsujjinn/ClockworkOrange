package com.clockworkred.app

import com.clockworkred.data.repository.FakeArrangementRepository
import com.clockworkred.data.repository.FakeProjectRepository
import com.clockworkred.data.repository.SettingsRepositoryImpl
import com.clockworkred.data.repository.StyleRepositoryImpl
import com.clockworkred.data.repository.TheoryRepositoryImpl
import com.clockworkred.domain.ArrangementRepository
import com.clockworkred.domain.ProjectRepository
import com.clockworkred.domain.StyleRepository
import com.clockworkred.domain.repository.SettingsRepository
import com.clockworkred.domain.repository.TheoryRepository
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

    @Binds
    @Singleton
    abstract fun bindTheoryRepository(impl: TheoryRepositoryImpl): TheoryRepository

    @Binds
    @Singleton
    abstract fun bindStyleRepository(impl: StyleRepositoryImpl): StyleRepository
}
