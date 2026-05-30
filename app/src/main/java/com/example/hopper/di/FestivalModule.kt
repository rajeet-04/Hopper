package com.example.hopper.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.hopper.data.local.db.dao.CalendarDao
import com.example.hopper.data.repository.FestivalToggleControllerImpl
import com.example.hopper.domain.FestivalToggleController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FestivalDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationCoroutineScope

/**
 * Extension property for Context to create the festival DataStore instance.
 */
private val Context.festivalDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "festival_preferences"
)

/**
 * Hilt module providing FestivalToggleController and its dependencies.
 * Provides the DataStore for persisting festival/year selection and
 * an application-scoped CoroutineScope for initialization work.
 */
@Module
@InstallIn(SingletonComponent::class)
object FestivalModule {

    @Provides
    @Singleton
    @FestivalDataStore
    fun provideFestivalDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.festivalDataStore
    }

    @Provides
    @Singleton
    @ApplicationCoroutineScope
    fun provideApplicationCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideFestivalToggleController(
        @FestivalDataStore dataStore: DataStore<Preferences>,
        calendarDao: CalendarDao,
        @ApplicationCoroutineScope applicationScope: CoroutineScope
    ): FestivalToggleController {
        return FestivalToggleControllerImpl(
            dataStore = dataStore,
            calendarDao = calendarDao,
            applicationScope = applicationScope
        )
    }
}
