package com.example.hopper.di

import com.example.hopper.ui.map.MapEngineController
import com.example.hopper.ui.map.MapLibreEngineController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapModule {
    @Binds
    @Singleton
    abstract fun bindMapEngineController(impl: MapLibreEngineController): MapEngineController
}
