package com.example.hopper.domain.repository

import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.model.CrowdReport
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for crowd report operations.
 * Handles submission, aggregation, rate limiting, and expiry of crowd reports.
 */
interface CrowdReportRepository {

    /**
     * Submits a crowd report for a pandal.
     * Returns failure if the device is rate-limited (within 10 minutes of last report for same pandal).
     * Reports are queued locally with isSynced=false for later upload.
     */
    suspend fun submitReport(pandalId: String, bucket: CrowdBucket, deviceHash: String): Result<Unit>

    /**
     * Returns a Flow emitting the aggregated crowd bucket for a pandal using weighted median
     * of non-expired reports. Emits null if no active reports exist.
     */
    fun getAggregatedCrowd(pandalId: String): Flow<CrowdBucket?>

    /**
     * Returns a Flow of all active (non-expired) crowd reports for a pandal.
     */
    fun getActiveReports(pandalId: String): Flow<List<CrowdReport>>

    /**
     * Checks whether the device is rate-limited for reporting on a specific pandal.
     * Returns true if the device submitted a report within the last 10 minutes.
     */
    suspend fun isRateLimited(pandalId: String, deviceHash: String): Boolean

    /**
     * Removes all expired crowd reports from the local database.
     */
    suspend fun cleanupExpiredReports()

    /**
     * Returns all reports that have not yet been synced to the backend.
     */
    suspend fun getUnsyncedReports(): List<CrowdReport>

    /**
     * Marks a report as synced after successful upload to the backend.
     */
    suspend fun markSynced(reportId: String)
}
