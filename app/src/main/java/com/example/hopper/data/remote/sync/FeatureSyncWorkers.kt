package com.example.hopper.data.remote.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hopper.domain.repository.BhogRepository
import com.example.hopper.domain.repository.BishorjonRepository
import com.example.hopper.domain.repository.LostPersonRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BhogUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val bhogRepository: BhogRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val unsynced = bhogRepository.getUnsyncedPins()
            unsynced.forEach { bhogRepository.markSynced(it.id) }
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}

@HiltWorker
class ProcessionUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val bishorjonRepository: BishorjonRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val unsynced = bishorjonRepository.getUnsyncedReports()
            unsynced.forEach { bishorjonRepository.markReportSynced(it.id) }
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}

@HiltWorker
class LostPersonUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val lostPersonRepository: LostPersonRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val unsynced = lostPersonRepository.getUnsyncedPosts()
            unsynced.forEach { lostPersonRepository.markSynced(it.id) }
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
