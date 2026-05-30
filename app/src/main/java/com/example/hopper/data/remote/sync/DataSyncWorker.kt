package com.example.hopper.data.remote.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hopper.data.local.db.dao.PandalDao
import com.example.hopper.data.local.db.entity.PandalEntity
import com.example.hopper.data.remote.api.HopperApiService
import com.example.hopper.domain.FestivalToggleController
import com.example.hopper.domain.model.FestivalContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: HopperApiService,
    private val pandalDao: PandalDao,
    private val festivalToggleController: FestivalToggleController
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val context = festivalToggleController.activeFestivalContext.first()
            syncPandals(context)
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    private suspend fun syncPandals(context: FestivalContext) {
        val response = apiService.getPandals(
            festival = context.festival.name,
            year = context.year
        )
        if (response.isSuccessful) {
            val pandals = response.body()?.data ?: return
            val entities = pandals.map { dto ->
                PandalEntity(
                    id = dto.id,
                    name = dto.name,
                    nameBengali = dto.nameBengali,
                    latitude = dto.latitude,
                    longitude = dto.longitude,
                    city = dto.city,
                    neighborhood = dto.neighborhood,
                    festival = dto.festival,
                    year = dto.year,
                    theme = dto.theme,
                    committeeName = dto.committeeName,
                    establishedYear = dto.establishedYear,
                    artisanCreditsJson = "[]",
                    awards = "",
                    photos = "[]",
                    significanceRank = dto.significanceRank,
                    sourceType = dto.sourceType,
                    confidenceLevel = dto.confidenceLevel
                )
            }
            pandalDao.insertAll(entities)
        }
    }
}
