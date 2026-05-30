package com.example.hopper.data.remote.sync

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralizes scheduling of all periodic background work with unique names so
 * duplicate scheduling is prevented across app launches.
 */
@Singleton
class WorkScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    companion object {
        const val DATA_SYNC_WORK = "data_sync_work"
        const val CROWD_UPLOAD_WORK = "crowd_upload_work"
        const val BHOG_UPLOAD_WORK = "bhog_upload_work"
        const val PROCESSION_UPLOAD_WORK = "procession_upload_work"
        const val LOST_PERSON_UPLOAD_WORK = "lost_person_upload_work"
    }

    private val networkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()

    fun scheduleAll() {
        schedulePeriodic<DataSyncWorker>(DATA_SYNC_WORK)
        schedulePeriodic<CrowdUploadWorker>(CROWD_UPLOAD_WORK)
        schedulePeriodic<BhogUploadWorker>(BHOG_UPLOAD_WORK)
        schedulePeriodic<ProcessionUploadWorker>(PROCESSION_UPLOAD_WORK)
        schedulePeriodic<LostPersonUploadWorker>(LOST_PERSON_UPLOAD_WORK)
    }

    private inline fun <reified W : ListenableWorker> schedulePeriodic(
        uniqueName: String
    ) {
        val request = PeriodicWorkRequestBuilder<W>(15, TimeUnit.MINUTES)
            .setConstraints(networkConstraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.SECONDS
            )
            .build()
        workManager.enqueueUniquePeriodicWork(
            uniqueName,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
