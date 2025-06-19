package com.clockworkred.app

import com.clockworkred.data.repository.FakeArrangementRepository
import com.clockworkred.domain.ArrangementRepository
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
}
