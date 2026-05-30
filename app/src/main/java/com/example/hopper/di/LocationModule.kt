package com.example.hopper.di

import android.content.Context
import com.example.hopper.data.local.LocationProviderImpl
import com.example.hopper.domain.LocationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing location-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationProvider(impl: LocationProviderImpl): LocationProvider

    companion object {

        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(
            @ApplicationContext context: Context
        ): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(context)
        }
    }
}
