package com.example.hopper.di

import com.example.hopper.data.repository.PandalRepositoryImpl
import com.example.hopper.domain.repository.PandalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing pandal-related repository bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class PandalModule {

    @Binds
    @Singleton
    abstract fun bindPandalRepository(impl: PandalRepositoryImpl): PandalRepository
}
