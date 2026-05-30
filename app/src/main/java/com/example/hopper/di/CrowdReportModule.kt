package com.example.hopper.di

import com.example.hopper.data.repository.CrowdReportRepositoryImpl
import com.example.hopper.domain.repository.CrowdReportRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

/**
 * Hilt module providing crowd report dependencies including the repository
 * binding and a system Clock instance for testability.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CrowdReportModule {

    @Binds
    @Singleton
    abstract fun bindCrowdReportRepository(impl: CrowdReportRepositoryImpl): CrowdReportRepository

    companion object {

        @Provides
        @Singleton
        fun provideClock(): Clock {
            return Clock.systemUTC()
        }
    }
}
