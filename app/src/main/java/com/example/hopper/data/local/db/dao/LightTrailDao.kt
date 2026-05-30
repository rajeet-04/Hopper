package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.LightTrailEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for light trail (Chandannagar) operations.
 */
@Dao
interface LightTrailDao {

    @Query("SELECT * FROM light_trails WHERE year = :year ORDER BY sequence_order ASC")
    fun getByYear(year: Int): Flow<List<LightTrailEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trails: List<LightTrailEntity>)

    @Query("DELETE FROM light_trails")
    suspend fun deleteAll()
}
