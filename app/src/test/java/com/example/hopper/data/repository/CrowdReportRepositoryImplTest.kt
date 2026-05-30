package com.example.hopper.data.repository

import app.cash.turbine.test
import com.example.hopper.data.local.db.dao.CrowdReportDao
import com.example.hopper.data.local.db.dao.ReputationDao
import com.example.hopper.data.local.db.entity.CrowdReportEntity
import com.example.hopper.data.local.db.entity.ReputationEntity
import com.example.hopper.domain.model.CrowdBucket
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class CrowdReportRepositoryImplTest {

    private lateinit var crowdReportDao: CrowdReportDao
    private lateinit var reputationDao: ReputationDao
    private lateinit var clock: Clock
    private lateinit var repository: CrowdReportRepositoryImpl

    private val fixedInstant = Instant.parse("2026-10-10T18:00:00Z")
    private val fixedMillis = fixedInstant.toEpochMilli()

    @Before
    fun setup() {
        crowdReportDao = mockk(relaxed = true)
        reputationDao = mockk(relaxed = true)
        clock = Clock.fixed(fixedInstant, ZoneOffset.UTC)
        repository = CrowdReportRepositoryImpl(crowdReportDao, reputationDao, clock)
    }

    // --- submitReport tests ---

    @Test
    fun `submitReport succeeds when not rate limited`() = runTest {
        coEvery { crowdReportDao.getLatestByDeviceAndPandal("device1", "pandal1") } returns null

        val result = repository.submitReport("pandal1", CrowdBucket.GREEN, "device1")

        assertThat(result.isSuccess).isTrue()
        coVerify { crowdReportDao.insert(any()) }
    }

    @Test
    fun `submitReport inserts entity with correct fields`() = runTest {
        coEvery { crowdReportDao.getLatestByDeviceAndPandal("device1", "pandal1") } returns null
        val entitySlot = slot<CrowdReportEntity>()
        coEvery { crowdReportDao.insert(capture(entitySlot)) } returns Unit

        repository.submitReport("pandal1", CrowdBucket.YELLOW, "device1")

        val entity = entitySlot.captured
        assertThat(entity.pandalId).isEqualTo("pandal1")
        assertThat(entity.bucket).isEqualTo("YELLOW")
        assertThat(entity.deviceHash).isEqualTo("device1")
        assertThat(entity.reportedAtEpochMs).isEqualTo(fixedMillis)
        assertThat(entity.expiresAtEpochMs).isEqualTo(fixedMillis + 20 * 60 * 1000)
        assertThat(entity.isSynced).isFalse()
        assertThat(entity.id).isNotEmpty()
    }

    @Test
    fun `submitReport fails when rate limited`() = runTest {
        val recentReport = CrowdReportEntity(
            id = "existing",
            pandalId = "pandal1",
            bucket = "GREEN",
            deviceHash = "device1",
            reportedAtEpochMs = fixedMillis - 5 * 60 * 1000, // 5 minutes ago
            expiresAtEpochMs = fixedMillis + 15 * 60 * 1000,
            isSynced = false
        )
        coEvery { crowdReportDao.getLatestByDeviceAndPandal("device1", "pandal1") } returns recentReport

        val result = repository.submitReport("pandal1", CrowdBucket.RED, "device1")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(RateLimitedException::class.java)
    }

    @Test
    fun `submitReport succeeds when last report is older than 10 minutes`() = runTest {
        val oldReport = CrowdReportEntity(
            id = "old",
            pandalId = "pandal1",
            bucket = "GREEN",
            deviceHash = "device1",
            reportedAtEpochMs = fixedMillis - 11 * 60 * 1000, // 11 minutes ago
            expiresAtEpochMs = fixedMillis - 1 * 60 * 1000,
            isSynced = true
        )
        coEvery { crowdReportDao.getLatestByDeviceAndPandal("device1", "pandal1") } returns oldReport

        val result = repository.submitReport("pandal1", CrowdBucket.RED, "device1")

        assertThat(result.isSuccess).isTrue()
        coVerify { crowdReportDao.insert(any()) }
    }

    // --- isRateLimited tests ---

    @Test
    fun `isRateLimited returns false when no previous report exists`() = runTest {
        coEvery { crowdReportDao.getLatestByDeviceAndPandal("device1", "pandal1") } returns null

        val result = repository.isRateLimited("pandal1", "device1")

        assertThat(result).isFalse()
    }

    @Test
    fun `isRateLimited returns true when within 10 minute window`() = runTest {
        val recentReport = CrowdReportEntity(
            id = "recent",
            pandalId = "pandal1",
            bucket = "YELLOW",
            deviceHash = "device1",
            reportedAtEpochMs = fixedMillis - 9 * 60 * 1000, // 9 minutes ago
            expiresAtEpochMs = fixedMillis + 11 * 60 * 1000,
            isSynced = false
        )
        coEvery { crowdReportDao.getLatestByDeviceAndPandal("device1", "pandal1") } returns recentReport

        val result = repository.isRateLimited("pandal1", "device1")

        assertThat(result).isTrue()
    }

    @Test
    fun `isRateLimited returns false when exactly at 10 minute boundary`() = runTest {
        val boundaryReport = CrowdReportEntity(
            id = "boundary",
            pandalId = "pandal1",
            bucket = "GREEN",
            deviceHash = "device1",
            reportedAtEpochMs = fixedMillis - 10 * 60 * 1000, // exactly 10 minutes ago
            expiresAtEpochMs = fixedMillis + 10 * 60 * 1000,
            isSynced = true
        )
        coEvery { crowdReportDao.getLatestByDeviceAndPandal("device1", "pandal1") } returns boundaryReport

        val result = repository.isRateLimited("pandal1", "device1")

        assertThat(result).isFalse()
    }

    // --- getAggregatedCrowd tests ---

    @Test
    fun `getAggregatedCrowd returns null when no active reports`() = runTest {
        every { crowdReportDao.getActiveReportsForPandal("pandal1", fixedMillis) } returns flowOf(emptyList())
        coEvery { reputationDao.getByDeviceHash(any()) } returns null

        repository.getAggregatedCrowd("pandal1").test {
            assertThat(awaitItem()).isNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getAggregatedCrowd returns single report bucket when only one report`() = runTest {
        val reports = listOf(
            makeEntity("r1", "pandal1", "RED", "device1")
        )
        every { crowdReportDao.getActiveReportsForPandal("pandal1", fixedMillis) } returns flowOf(reports)
        coEvery { reputationDao.getByDeviceHash(any()) } returns null

        repository.getAggregatedCrowd("pandal1").test {
            assertThat(awaitItem()).isEqualTo(CrowdBucket.RED)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getAggregatedCrowd returns weighted median with equal weights`() = runTest {
        // 2 GREEN, 1 YELLOW, 2 RED — median should be YELLOW
        val reports = listOf(
            makeEntity("r1", "pandal1", "GREEN", "d1"),
            makeEntity("r2", "pandal1", "GREEN", "d2"),
            makeEntity("r3", "pandal1", "YELLOW", "d3"),
            makeEntity("r4", "pandal1", "RED", "d4"),
            makeEntity("r5", "pandal1", "RED", "d5")
        )
        every { crowdReportDao.getActiveReportsForPandal("pandal1", fixedMillis) } returns flowOf(reports)
        coEvery { reputationDao.getByDeviceHash(any()) } returns null

        repository.getAggregatedCrowd("pandal1").test {
            assertThat(awaitItem()).isEqualTo(CrowdBucket.YELLOW)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getAggregatedCrowd applies reputation weight multiplier`() = runTest {
        // 1 GREEN (weight 2.0), 1 RED (weight 1.0) — total weight 3.0, median at 1.5
        // Sorted: GREEN(2.0), RED(1.0)
        // Cumulative: GREEN reaches 2.0 >= 1.5 → GREEN
        val reports = listOf(
            makeEntity("r1", "pandal1", "GREEN", "high_rep_device"),
            makeEntity("r2", "pandal1", "RED", "low_rep_device")
        )
        every { crowdReportDao.getActiveReportsForPandal("pandal1", fixedMillis) } returns flowOf(reports)
        coEvery { reputationDao.getByDeviceHash("high_rep_device") } returns makeReputation("high_rep_device", 0.95, 2.0)
        coEvery { reputationDao.getByDeviceHash("low_rep_device") } returns null

        repository.getAggregatedCrowd("pandal1").test {
            assertThat(awaitItem()).isEqualTo(CrowdBucket.GREEN)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getAggregatedCrowd majority GREEN returns GREEN`() = runTest {
        val reports = listOf(
            makeEntity("r1", "pandal1", "GREEN", "d1"),
            makeEntity("r2", "pandal1", "GREEN", "d2"),
            makeEntity("r3", "pandal1", "GREEN", "d3")
        )
        every { crowdReportDao.getActiveReportsForPandal("pandal1", fixedMillis) } returns flowOf(reports)
        coEvery { reputationDao.getByDeviceHash(any()) } returns null

        repository.getAggregatedCrowd("pandal1").test {
            assertThat(awaitItem()).isEqualTo(CrowdBucket.GREEN)
            cancelAndConsumeRemainingEvents()
        }
    }

    // --- getActiveReports tests ---

    @Test
    fun `getActiveReports maps entities to domain models`() = runTest {
        val entities = listOf(
            makeEntity("r1", "pandal1", "GREEN", "device1"),
            makeEntity("r2", "pandal1", "RED", "device2")
        )
        every { crowdReportDao.getActiveReportsForPandal("pandal1", fixedMillis) } returns flowOf(entities)

        repository.getActiveReports("pandal1").test {
            val reports = awaitItem()
            assertThat(reports).hasSize(2)
            assertThat(reports[0].id).isEqualTo("r1")
            assertThat(reports[0].bucket).isEqualTo(CrowdBucket.GREEN)
            assertThat(reports[1].id).isEqualTo("r2")
            assertThat(reports[1].bucket).isEqualTo(CrowdBucket.RED)
            cancelAndConsumeRemainingEvents()
        }
    }

    // --- cleanupExpiredReports tests ---

    @Test
    fun `cleanupExpiredReports calls dao deleteExpired with current time`() = runTest {
        repository.cleanupExpiredReports()

        coVerify { crowdReportDao.deleteExpired(fixedMillis) }
    }

    // --- getUnsyncedReports tests ---

    @Test
    fun `getUnsyncedReports returns mapped domain models`() = runTest {
        val entities = listOf(
            makeEntity("r1", "pandal1", "YELLOW", "device1", isSynced = false)
        )
        coEvery { crowdReportDao.getUnsyncedReports() } returns entities

        val result = repository.getUnsyncedReports()

        assertThat(result).hasSize(1)
        assertThat(result[0].id).isEqualTo("r1")
        assertThat(result[0].isSynced).isFalse()
    }

    // --- markSynced tests ---

    @Test
    fun `markSynced delegates to dao`() = runTest {
        repository.markSynced("report-123")

        coVerify { crowdReportDao.markSynced("report-123") }
    }

    // --- Helper functions ---

    private fun makeEntity(
        id: String,
        pandalId: String,
        bucket: String,
        deviceHash: String,
        isSynced: Boolean = false
    ): CrowdReportEntity {
        return CrowdReportEntity(
            id = id,
            pandalId = pandalId,
            bucket = bucket,
            deviceHash = deviceHash,
            reportedAtEpochMs = fixedMillis - 5 * 60 * 1000,
            expiresAtEpochMs = fixedMillis + 15 * 60 * 1000,
            isSynced = isSynced
        )
    }

    private fun makeReputation(
        deviceHash: String,
        accuracyScore: Double,
        weightMultiplier: Double
    ): ReputationEntity {
        return ReputationEntity(
            id = deviceHash,
            totalReports = 50,
            accurateReports = 45,
            accuracyScore = accuracyScore,
            badgeTier = "GOLD",
            weightMultiplier = weightMultiplier
        )
    }
}
