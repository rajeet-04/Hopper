package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.AudioAssetDao
import com.example.hopper.data.local.db.dao.RitualGuideDao
import com.example.hopper.data.local.db.entity.AudioAssetEntity
import com.example.hopper.data.local.db.entity.RitualGuideEntity
import com.example.hopper.domain.repository.RitualGuideRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RitualGuideRepositoryImpl @Inject constructor(
    private val ritualGuideDao: RitualGuideDao,
    private val audioAssetDao: AudioAssetDao,
    private val clock: Clock
) : RitualGuideRepository {

    override fun getByFestivalAndYear(festival: String, year: Int): Flow<List<RitualGuideEntity>> =
        ritualGuideDao.getByFestivalAndYear(festival, year)

    override suspend fun getByTithiId(tithiId: String): RitualGuideEntity? =
        ritualGuideDao.getByTithiId(tithiId)

    override suspend fun getAudioAsset(assetId: String): AudioAssetEntity? =
        audioAssetDao.getById(assetId)

    override suspend fun downloadAudio(assetId: String, url: String, filesDir: File): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val dir = File(filesDir, "ritual_audio")
                if (!dir.exists()) dir.mkdirs()

                val fileName = "${assetId}.mp3"
                val file = File(dir, fileName)

                URL(url).openStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                val localPath = file.absolutePath
                val fileSize = file.length()

                audioAssetDao.upsert(
                    AudioAssetEntity(
                        id = assetId,
                        url = url,
                        localPath = localPath,
                        fileSizeBytes = fileSize,
                        isCached = true,
                        lastAccessedEpochMs = clock.millis()
                    )
                )

                Result.success(localPath)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getCacheSize(): Long =
        audioAssetDao.getTotalCacheSize()

    override suspend fun clearCache() =
        audioAssetDao.clearCache()

    override suspend fun insertAll(guides: List<RitualGuideEntity>) =
        ritualGuideDao.insertAll(guides)
}
