package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.ProcessionDao
import com.example.hopper.data.local.db.entity.ProcessionEntity
import com.example.hopper.data.local.db.entity.ProcessionReportEntity
import com.example.hopper.domain.repository.BishorjonRepository
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BishorjonRepositoryImpl @Inject constructor(
    private val processionDao: ProcessionDao,
    private val clock: Clock
) : BishorjonRepository {

    companion object {
        private const val STALE_THRESHOLD_MS = 15L * 60 * 1000
    }

    override fun getActiveProcessions(year: Int): Flow<List<ProcessionEntity>> =
        processionDao.getActiveProcessions(year)

    override suspend fun submitProcessionReport(report: ProcessionReportEntity) =
        processionDao.insertReport(report)

    override suspend fun getReportsForProcession(processionId: String): List<ProcessionReportEntity> {
        val cutoff = clock.millis() - STALE_THRESHOLD_MS
        return processionDao.getReportsForProcession(processionId, cutoff)
    }

    override suspend fun getUnsyncedReports(): List<ProcessionReportEntity> =
        processionDao.getUnsyncedReports()

    override suspend fun markReportSynced(id: String) =
        processionDao.markReportSynced(id)

    override suspend fun insertAll(processions: List<ProcessionEntity>) =
        processionDao.insertAll(processions)
}
