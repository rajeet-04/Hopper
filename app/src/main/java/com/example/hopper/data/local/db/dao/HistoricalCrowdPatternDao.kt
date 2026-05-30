package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.HistoricalCrowdPatternEntity

/**
 * Data Access Object for historical crowd pattern operations.
 */
@Dao
interface HistoricalCrowdPatternDao {

    @Query("SELECT * FROM historical_crowd_patterns WHERE pandal_id = :pandalId AND day_of_festival = :dayOfFestival ORDER BY hour_of_day ASC")
    suspend fun getByPandalAndDay(pandalId: String, dayOfFestival: Int): List<HistoricalCrowdPatternEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patterns: List<HistoricalCrowdPatternEntity>)

    @Query("DELETE FROM historical_crowd_patterns")
    suspend fun deleteAll()
}
