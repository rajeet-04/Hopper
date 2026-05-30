package com.example.hopper.di

import android.content.Context
import android.content.SharedPreferences
import com.example.hopper.util.LocaleManager
import com.example.hopper.util.StringProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing locale-related singletons:
 * SharedPreferences for locale persistence, LocaleManager, and StringProvider.
 */
@Module
@InstallIn(SingletonComponent::class)
object LocaleModule {

    @Provides
    @Singleton
    fun provideLocalePreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("locale_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideLocaleManager(preferences: SharedPreferences): LocaleManager {
        return LocaleManager(preferences)
    }

    @Provides
    @Singleton
    fun provideStringProvider(localeManager: LocaleManager): StringProvider {
        return StringProvider(localeManager)
    }
}
