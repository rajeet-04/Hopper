package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.CrowdReportDao
import com.example.hopper.data.local.db.dao.ReputationDao
import com.example.hopper.data.local.db.entity.CrowdReportEntity
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.model.CrowdReport
import com.example.hopper.domain.repository.CrowdReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [CrowdReportRepository] backed by Room database.
 *
 * Handles crowd report submission with rate limiting, expiry cleanup,
 * and weighted median aggregation using reporter reputation weights.
 *
 * Privacy: reports contain ONLY pandalId, bucket, deviceHash, timestamp — no PII.
 */
@Singleton
class CrowdReportRepositoryImpl @Inject constructor(
    private val crowdReportDao: CrowdReportDao,
    private val reputationDao: ReputationDao,
    private val clock: Clock
) : CrowdReportRepository {

    companion object {
        /** Rate limit window: 10 minutes per pandal per device. */
        private const val RATE_LIMIT_MS = 10L * 60 * 1000

        /** Report expiry duration: 20 minutes. */
        private const val EXPIRY_DURATION_MS = 20L * 60 * 1000
    }

    override suspend fun submitReport(
        pandalId: String,
        bucket: CrowdBucket,
        deviceHash: String
    ): Result<Unit> {
        if (isRateLimited(pandalId, deviceHash)) {
            return Result.failure(RateLimitedException(pandalId, deviceHash))
        }

        val now = clock.millis()
        val entity = CrowdReportEntity(
            id = UUID.randomUUID().toString(),
            pandalId = pandalId,
            bucket = bucket.name,
            deviceHash = deviceHash,
            reportedAtEpochMs = now,
            expiresAtEpochMs = now + EXPIRY_DURATION_MS,
            isSynced = false
        )

        crowdReportDao.insert(entity)
        return Result.success(Unit)
    }

    override fun getAggregatedCrowd(pandalId: String): Flow<CrowdBucket?> {
        val now = clock.millis()
        return crowdReportDao.getActiveReportsForPandal(pandalId, now).map { entities ->
            if (entities.isEmpty()) {
                null
            } else {
                calculateWeightedMedian(entities)
            }
        }
    }

    override fun getActiveReports(pandalId: String): Flow<List<CrowdReport>> {
        val now = clock.millis()
        return crowdReportDao.getActiveReportsForPandal(pandalId, now).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun isRateLimited(pandalId: String, deviceHash: String): Boolean {
        val latest = crowdReportDao.getLatestByDeviceAndPandal(deviceHash, pandalId)
            ?: return false
        val now = clock.millis()
        return (now - latest.reportedAtEpochMs) < RATE_LIMIT_MS
    }

    override suspend fun cleanupExpiredReports() {
        crowdReportDao.deleteExpired(clock.millis())
    }

    override suspend fun getUnsyncedReports(): List<CrowdReport> {
        return crowdReportDao.getUnsyncedReports().map { it.toDomain() }
    }

    override suspend fun markSynced(reportId: String) {
        crowdReportDao.markSynced(reportId)
    }

    /**
     * Calculates the weighted median crowd bucket from a list of active reports.
     *
     * Each report's weight is determined by the reporter's reputation:
     * - Default weight: 1.0
     * - Accuracy > 0.7: weight = 1.5
     * - Accuracy > 0.9: weight = 2.0
     *
     * Reports are sorted by bucket ordinal (GREEN=0, YELLOW=1, RED=2),
     * and the bucket at the weighted median position is returned.
     */
    private suspend fun calculateWeightedMedian(entities: List<CrowdReportEntity>): CrowdBucket {
        // Build weighted entries: pair of (bucket, weight)
        val weightedEntries = entities.map { entity ->
            val bucket = CrowdBucket.valueOf(entity.bucket)
            val weight = getReporterWeight(entity.deviceHash)
            bucket to weight
        }

        // Sort by bucket ordinal (GREEN=0, YELLOW=1, RED=2)
        val sorted = weightedEntries.sortedBy { it.first.ordinal }

        // Calculate total weight
        val totalWeight = sorted.sumOf { it.second }

        // Find the weighted median position
        val medianPosition = totalWeight / 2.0
        var cumulativeWeight = 0.0

        for ((bucket, weight) in sorted) {
            cumulativeWeight += weight
            if (cumulativeWeight >= medianPosition) {
                return bucket
            }
        }

        // Fallback: return the last bucket (should not reach here)
        return sorted.last().first
    }

    /**
     * Gets the reputation-based weight multiplier for a reporter.
     * Falls back to 1.0 if no reputation record exists.
     */
    private suspend fun getReporterWeight(deviceHash: String): Double {
        val reputation = reputationDao.getByDeviceHash(deviceHash)
        return reputation?.weightMultiplier ?: 1.0
    }

    private fun CrowdReportEntity.toDomain(): CrowdReport {
        return CrowdReport(
            id = id,
            pandalId = pandalId,
            bucket = CrowdBucket.valueOf(bucket),
            deviceHash = deviceHash,
            reportedAt = Instant.ofEpochMilli(reportedAtEpochMs),
            expiresAt = Instant.ofEpochMilli(expiresAtEpochMs),
            isSynced = isSynced
        )
    }
}

/**
 * Exception thrown when a device attempts to submit a crowd report
 * within the rate limit window (10 minutes per pandal per device).
 */
class RateLimitedException(
    val pandalId: String,
    val deviceHash: String
) : Exception("Rate limited: device $deviceHash already reported for pandal $pandalId within the last 10 minutes")
