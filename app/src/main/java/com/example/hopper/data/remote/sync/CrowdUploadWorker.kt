package com.example.hopper.data.remote.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hopper.data.remote.api.HopperApiService
import com.example.hopper.data.remote.api.dto.CrowdReportDto
import com.example.hopper.domain.repository.CrowdReportRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CrowdUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val crowdReportRepository: CrowdReportRepository,
    private val apiService: HopperApiService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val unsyncedReports = crowdReportRepository.getUnsyncedReports()
            unsyncedReports.forEach { report ->
                val dto = CrowdReportDto(
                    pandalId = report.pandalId,
                    bucket = report.bucket.name,
                    deviceHash = report.deviceHash,
                    reportedAt = report.reportedAt.toEpochMilli()
                )
                val response = apiService.submitCrowdReport(dto)
                if (response.isSuccessful) {
                    crowdReportRepository.markSynced(report.id)
                }
            }
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
