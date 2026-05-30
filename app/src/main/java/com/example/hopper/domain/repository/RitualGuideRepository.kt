package com.example.hopper.domain.repository

import com.example.hopper.data.local.db.entity.AudioAssetEntity
import com.example.hopper.data.local.db.entity.RitualGuideEntity
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RitualGuideRepository {
    fun getByFestivalAndYear(festival: String, year: Int): Flow<List<RitualGuideEntity>>
    suspend fun getByTithiId(tithiId: String): RitualGuideEntity?
    suspend fun getAudioAsset(assetId: String): AudioAssetEntity?
    suspend fun downloadAudio(assetId: String, url: String, filesDir: File): Result<String>
    suspend fun getCacheSize(): Long
    suspend fun clearCache()
    suspend fun insertAll(guides: List<RitualGuideEntity>)
}
