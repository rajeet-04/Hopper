package com.example.hopper.di

import com.example.hopper.data.repository.BhogRepositoryImpl
import com.example.hopper.data.repository.BishorjonRepositoryImpl
import com.example.hopper.data.repository.HeritageRepositoryImpl
import com.example.hopper.data.repository.LightTrailRepositoryImpl
import com.example.hopper.data.repository.LostPersonRepositoryImpl
import com.example.hopper.data.repository.OralHistoryRepositoryImpl
import com.example.hopper.data.repository.ReputationRepositoryImpl
import com.example.hopper.data.repository.RitualGuideRepositoryImpl
import com.example.hopper.data.repository.VolunteerRepositoryImpl
import com.example.hopper.domain.repository.BhogRepository
import com.example.hopper.domain.repository.BishorjonRepository
import com.example.hopper.domain.repository.HeritageRepository
import com.example.hopper.domain.repository.LightTrailRepository
import com.example.hopper.domain.repository.LostPersonRepository
import com.example.hopper.domain.repository.OralHistoryRepository
import com.example.hopper.domain.repository.ReputationRepository
import com.example.hopper.domain.repository.RitualGuideRepository
import com.example.hopper.domain.repository.VolunteerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExtendedRepositoryModule {

    @Binds @Singleton
    abstract fun bindLightTrailRepository(impl: LightTrailRepositoryImpl): LightTrailRepository

    @Binds @Singleton
    abstract fun bindBishorjonRepository(impl: BishorjonRepositoryImpl): BishorjonRepository

    @Binds @Singleton
    abstract fun bindBhogRepository(impl: BhogRepositoryImpl): BhogRepository

    @Binds @Singleton
    abstract fun bindLostPersonRepository(impl: LostPersonRepositoryImpl): LostPersonRepository

    @Binds @Singleton
    abstract fun bindOralHistoryRepository(impl: OralHistoryRepositoryImpl): OralHistoryRepository

    @Binds @Singleton
    abstract fun bindHeritageRepository(impl: HeritageRepositoryImpl): HeritageRepository

    @Binds @Singleton
    abstract fun bindReputationRepository(impl: ReputationRepositoryImpl): ReputationRepository

    @Binds @Singleton
    abstract fun bindVolunteerRepository(impl: VolunteerRepositoryImpl): VolunteerRepository

    @Binds @Singleton
    abstract fun bindRitualGuideRepository(impl: RitualGuideRepositoryImpl): RitualGuideRepository
}
