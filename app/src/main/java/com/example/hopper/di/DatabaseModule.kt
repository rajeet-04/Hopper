package com.example.hopper.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import androidx.room.Room
import com.example.hopper.data.local.assets.GeoJsonAssetLoader
import com.example.hopper.data.local.db.DatabasePrepopulateCallback
import com.example.hopper.data.local.db.HopperDatabase
import com.example.hopper.data.local.db.dao.AudioAssetDao
import com.example.hopper.data.local.db.dao.BhogDao
import com.example.hopper.data.local.db.dao.CalendarDao
import com.example.hopper.data.local.db.dao.ConnectorDao
import com.example.hopper.data.local.db.dao.CrowdReportDao
import com.example.hopper.data.local.db.dao.EditionDao
import com.example.hopper.data.local.db.dao.ExitNodeDao
import com.example.hopper.data.local.db.dao.HeritageDao
import com.example.hopper.data.local.db.dao.HistoricalCrowdPatternDao
import com.example.hopper.data.local.db.dao.ItineraryDao
import com.example.hopper.data.local.db.dao.LightTrailDao
import com.example.hopper.data.local.db.dao.LostPersonDao
import com.example.hopper.data.local.db.dao.OralHistoryDao
import com.example.hopper.data.local.db.dao.PandalDao
import com.example.hopper.data.local.db.dao.ProcessionDao
import com.example.hopper.data.local.db.dao.ReputationDao
import com.example.hopper.data.local.db.dao.RitualGuideDao
import com.example.hopper.data.local.db.dao.VolunteerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatabasePrefs

/**
 * Hilt module providing the Room database singleton and all DAO instances.
 * Also provides dependencies needed for first-launch database prepopulation.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAssetManager(@ApplicationContext context: Context): AssetManager {
        return context.assets
    }

    @Provides
    @Singleton
    @DatabasePrefs
    fun provideDatabaseSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("hopper_database_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideDatabasePrepopulateCallback(
        databaseProvider: Provider<HopperDatabase>,
        assetLoader: GeoJsonAssetLoader,
        @DatabasePrefs sharedPreferences: SharedPreferences
    ): DatabasePrepopulateCallback {
        return DatabasePrepopulateCallback(
            databaseProvider = databaseProvider,
            assetLoader = assetLoader,
            sharedPreferences = sharedPreferences
        )
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        prepopulateCallback: DatabasePrepopulateCallback
    ): HopperDatabase {
        return Room.databaseBuilder(
            context,
            HopperDatabase::class.java,
            "hopper_database"
        )
            .addCallback(prepopulateCallback)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun providePandalDao(database: HopperDatabase): PandalDao {
        return database.pandalDao()
    }

    @Provides
    @Singleton
    fun provideExitNodeDao(database: HopperDatabase): ExitNodeDao {
        return database.exitNodeDao()
    }

    @Provides
    @Singleton
    fun provideConnectorDao(database: HopperDatabase): ConnectorDao {
        return database.connectorDao()
    }

    @Provides
    @Singleton
    fun provideCrowdReportDao(database: HopperDatabase): CrowdReportDao {
        return database.crowdReportDao()
    }

    @Provides
    @Singleton
    fun provideCalendarDao(database: HopperDatabase): CalendarDao {
        return database.calendarDao()
    }

    @Provides
    @Singleton
    fun provideEditionDao(database: HopperDatabase): EditionDao {
        return database.editionDao()
    }

    @Provides
    @Singleton
    fun provideItineraryDao(database: HopperDatabase): ItineraryDao {
        return database.itineraryDao()
    }

    @Provides
    @Singleton
    fun provideLightTrailDao(database: HopperDatabase): LightTrailDao {
        return database.lightTrailDao()
    }

    @Provides
    @Singleton
    fun provideBhogDao(database: HopperDatabase): BhogDao {
        return database.bhogDao()
    }

    @Provides
    @Singleton
    fun provideProcessionDao(database: HopperDatabase): ProcessionDao {
        return database.processionDao()
    }

    @Provides
    @Singleton
    fun provideHistoricalCrowdPatternDao(database: HopperDatabase): HistoricalCrowdPatternDao {
        return database.historicalCrowdPatternDao()
    }

    @Provides
    @Singleton
    fun provideLostPersonDao(database: HopperDatabase): LostPersonDao {
        return database.lostPersonDao()
    }

    @Provides
    @Singleton
    fun provideOralHistoryDao(database: HopperDatabase): OralHistoryDao {
        return database.oralHistoryDao()
    }

    @Provides
    @Singleton
    fun provideHeritageDao(database: HopperDatabase): HeritageDao {
        return database.heritageDao()
    }

    @Provides
    @Singleton
    fun provideReputationDao(database: HopperDatabase): ReputationDao {
        return database.reputationDao()
    }

    @Provides
    @Singleton
    fun provideVolunteerDao(database: HopperDatabase): VolunteerDao {
        return database.volunteerDao()
    }

    @Provides
    @Singleton
    fun provideRitualGuideDao(database: HopperDatabase): RitualGuideDao {
        return database.ritualGuideDao()
    }

    @Provides
    @Singleton
    fun provideAudioAssetDao(database: HopperDatabase): AudioAssetDao {
        return database.audioAssetDao()
    }
}
