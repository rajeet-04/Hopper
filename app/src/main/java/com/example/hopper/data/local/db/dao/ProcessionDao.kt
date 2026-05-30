package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.ProcessionEntity
import com.example.hopper.data.local.db.entity.ProcessionReportEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for procession (bishorjon) operations.
 */
@Dao
interface ProcessionDao {

    @Query("SELECT * FROM processions WHERE year = :year")
    fun getActiveProcessions(year: Int): Flow<List<ProcessionEntity>>

    @Query("SELECT * FROM procession_reports WHERE procession_id = :processionId AND reported_at_epoch_ms > (:currentTimeMs - 900000)")
    suspend fun getReportsForProcession(processionId: String, currentTimeMs: Long): List<ProcessionReportEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ProcessionReportEntity)

    @Query("SELECT * FROM procession_reports WHERE is_synced = 0")
    suspend fun getUnsyncedReports(): List<ProcessionReportEntity>

    @Query("UPDATE procession_reports SET is_synced = 1 WHERE id = :id")
    suspend fun markReportSynced(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(processions: List<ProcessionEntity>)
}
