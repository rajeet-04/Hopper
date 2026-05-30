package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.CrowdReportEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for crowd report operations.
 */
@Dao
interface CrowdReportDao {

    @Query("SELECT * FROM crowd_reports WHERE pandal_id = :pandalId AND expires_at_epoch_ms > :currentTimeMs")
    fun getActiveReportsForPandal(pandalId: String, currentTimeMs: Long): Flow<List<CrowdReportEntity>>

    @Query("SELECT * FROM crowd_reports WHERE device_hash = :deviceHash AND pandal_id = :pandalId ORDER BY reported_at_epoch_ms DESC LIMIT 1")
    suspend fun getLatestByDeviceAndPandal(deviceHash: String, pandalId: String): CrowdReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: CrowdReportEntity)

    @Query("DELETE FROM crowd_reports WHERE expires_at_epoch_ms <= :currentTimeMs")
    suspend fun deleteExpired(currentTimeMs: Long)

    @Query("SELECT * FROM crowd_reports WHERE is_synced = 0")
    suspend fun getUnsyncedReports(): List<CrowdReportEntity>

    @Query("UPDATE crowd_reports SET is_synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
