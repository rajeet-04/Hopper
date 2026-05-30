package com.example.hopper.domain.repository

import com.example.hopper.data.local.db.entity.ProcessionEntity
import com.example.hopper.data.local.db.entity.ProcessionReportEntity
import kotlinx.coroutines.flow.Flow

interface BishorjonRepository {
    fun getActiveProcessions(year: Int): Flow<List<ProcessionEntity>>
    suspend fun submitProcessionReport(report: ProcessionReportEntity)
    suspend fun getReportsForProcession(processionId: String): List<ProcessionReportEntity>
    suspend fun getUnsyncedReports(): List<ProcessionReportEntity>
    suspend fun markReportSynced(id: String)
    suspend fun insertAll(processions: List<ProcessionEntity>)
}
