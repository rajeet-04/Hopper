package com.example.hopper.data.remote.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hopper.domain.repository.CrowdReportRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CrowdUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val crowdReportRepository: CrowdReportRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val unsyncedReports = crowdReportRepository.getUnsyncedReports()
            unsyncedReports.forEach { report ->
                // TODO: Upload to backend API when available
                crowdReportRepository.markSynced(report.id)
            }
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
