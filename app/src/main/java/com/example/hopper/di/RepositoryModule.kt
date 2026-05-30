package com.example.hopper.di

import com.example.hopper.data.repository.CalendarRepositoryImpl
import com.example.hopper.data.repository.ExitRouterRepositoryImpl
import com.example.hopper.domain.repository.CalendarRepository
import com.example.hopper.domain.repository.ExitRouterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing repository bindings for core domain repositories
 * not already bound in feature-specific modules.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExitRouterRepository(impl: ExitRouterRepositoryImpl): ExitRouterRepository

    @Binds
    @Singleton
    abstract fun bindCalendarRepository(impl: CalendarRepositoryImpl): CalendarRepository
}
