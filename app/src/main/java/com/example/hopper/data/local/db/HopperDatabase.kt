package com.example.hopper.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
import com.example.hopper.data.local.db.entity.AudioAssetEntity
import com.example.hopper.data.local.db.entity.BhogPinEntity
import com.example.hopper.data.local.db.entity.ConnectorEntity
import com.example.hopper.data.local.db.entity.CrowdReportEntity
import com.example.hopper.data.local.db.entity.EditionEntity
import com.example.hopper.data.local.db.entity.ExitNodeEntity
import com.example.hopper.data.local.db.entity.HeritagePointEntity
import com.example.hopper.data.local.db.entity.HistoricalCrowdPatternEntity
import com.example.hopper.data.local.db.entity.ItineraryEntity
import com.example.hopper.data.local.db.entity.ItineraryStopEntity
import com.example.hopper.data.local.db.entity.LightTrailEntity
import com.example.hopper.data.local.db.entity.LostPersonPostEntity
import com.example.hopper.data.local.db.entity.OralHistoryEntity
import com.example.hopper.data.local.db.entity.PandalEntity
import com.example.hopper.data.local.db.entity.ProcessionEntity
import com.example.hopper.data.local.db.entity.ProcessionReportEntity
import com.example.hopper.data.local.db.entity.ReputationEntity
import com.example.hopper.data.local.db.entity.RitualGuideEntity
import com.example.hopper.data.local.db.entity.TithiEntity
import com.example.hopper.data.local.db.entity.VolunteerPostEntity
import com.example.hopper.data.local.db.entity.VolunteerSignupEntity

/**
 * Main Room database for the Hopper application.
 * Contains all entities for offline-first festival navigation and archival.
 */
@Database(
    entities = [
        PandalEntity::class,
        ExitNodeEntity::class,
        ConnectorEntity::class,
        CrowdReportEntity::class,
        TithiEntity::class,
        EditionEntity::class,
        LightTrailEntity::class,
        BhogPinEntity::class,
        ProcessionEntity::class,
        ProcessionReportEntity::class,
        HistoricalCrowdPatternEntity::class,
        LostPersonPostEntity::class,
        OralHistoryEntity::class,
        HeritagePointEntity::class,
        ReputationEntity::class,
        VolunteerPostEntity::class,
        VolunteerSignupEntity::class,
        RitualGuideEntity::class,
        AudioAssetEntity::class,
        ItineraryEntity::class,
        ItineraryStopEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class HopperDatabase : RoomDatabase() {

    abstract fun pandalDao(): PandalDao

    abstract fun exitNodeDao(): ExitNodeDao

    abstract fun connectorDao(): ConnectorDao

    abstract fun crowdReportDao(): CrowdReportDao

    abstract fun calendarDao(): CalendarDao

    abstract fun editionDao(): EditionDao

    abstract fun itineraryDao(): ItineraryDao

    abstract fun lightTrailDao(): LightTrailDao

    abstract fun bhogDao(): BhogDao

    abstract fun processionDao(): ProcessionDao

    abstract fun historicalCrowdPatternDao(): HistoricalCrowdPatternDao

    abstract fun lostPersonDao(): LostPersonDao

    abstract fun oralHistoryDao(): OralHistoryDao

    abstract fun heritageDao(): HeritageDao

    abstract fun reputationDao(): ReputationDao

    abstract fun volunteerDao(): VolunteerDao

    abstract fun ritualGuideDao(): RitualGuideDao

    abstract fun audioAssetDao(): AudioAssetDao
}
