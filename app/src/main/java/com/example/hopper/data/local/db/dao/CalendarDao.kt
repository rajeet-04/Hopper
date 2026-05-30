package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.TithiEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for calendar/tithi operations.
 */
@Dao
interface CalendarDao {

    @Query("SELECT * FROM tithis WHERE festival = :festival AND year = :year ORDER BY date ASC")
    fun getTithisByFestivalAndYear(festival: String, year: Int): Flow<List<TithiEntity>>

    @Query("SELECT * FROM tithis WHERE date = :dateEpochMs LIMIT 1")
    suspend fun getTithiByDate(dateEpochMs: Long): TithiEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tithis: List<TithiEntity>)

    @Query("DELETE FROM tithis")
    suspend fun deleteAll()
}
