package com.example.hopper.domain.usecase

import com.example.hopper.data.local.db.entity.ProcessionEntity
import com.example.hopper.data.local.db.entity.ProcessionReportEntity
import com.example.hopper.domain.model.LatLng
import com.example.hopper.domain.repository.BishorjonRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProcessionTrackerUseCase @Inject constructor(
    private val bishorjonRepository: BishorjonRepository
) {
    fun observeProcessions(year: Int): Flow<List<ProcessionEntity>> =
        bishorjonRepository.getActiveProcessions(year)

    suspend fun reportSighting(processionId: String, location: LatLng, deviceHash: String) {
        val report = ProcessionReportEntity(
            id = UUID.randomUUID().toString(),
            processionId = processionId,
            latitude = location.latitude,
            longitude = location.longitude,
            deviceHash = deviceHash,
            reportedAtEpochMs = System.currentTimeMillis(),
            isSynced = false
        )
        bishorjonRepository.submitProcessionReport(report)
    }

    suspend fun getLatestReports(processionId: String): List<ProcessionReportEntity> =
        bishorjonRepository.getReportsForProcession(processionId)
}
