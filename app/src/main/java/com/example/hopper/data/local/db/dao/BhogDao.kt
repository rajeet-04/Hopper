package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.BhogPinEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for bhog (food distribution) pin operations.
 */
@Dao
interface BhogDao {

    @Query("SELECT * FROM bhog_pins WHERE category = :category AND (end_time_epoch_ms IS NULL OR end_time_epoch_ms > :currentTimeMs)")
    fun getActiveByCategory(category: String, currentTimeMs: Long): Flow<List<BhogPinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pin: BhogPinEntity)

    @Query("SELECT * FROM bhog_pins WHERE is_synced = 0")
    suspend fun getUnsyncedPins(): List<BhogPinEntity>

    @Query("UPDATE bhog_pins SET is_synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM bhog_pins WHERE end_time_epoch_ms IS NOT NULL AND end_time_epoch_ms <= :currentTimeMs")
    suspend fun deleteExpired(currentTimeMs: Long)
}
