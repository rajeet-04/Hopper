package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.OralHistoryDao
import com.example.hopper.data.local.db.entity.OralHistoryEntity
import com.example.hopper.domain.repository.OralHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OralHistoryRepositoryImpl @Inject constructor(
    private val oralHistoryDao: OralHistoryDao
) : OralHistoryRepository {

    override fun getAll(): Flow<List<OralHistoryEntity>> = oralHistoryDao.getAll()

    override fun getByPandalId(pandalId: String): Flow<List<OralHistoryEntity>> =
        oralHistoryDao.getByPandalId(pandalId)

    override suspend fun getById(id: String): OralHistoryEntity? = oralHistoryDao.getById(id)

    override suspend fun downloadAudio(id: String, url: String, filesDir: File): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val audioDir = File(filesDir, "oral_history")
                if (!audioDir.exists()) audioDir.mkdirs()
                val fileName = "${id}.mp3"
                val localFile = File(audioDir, fileName)
                URL(url).openStream().use { input ->
                    localFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                oralHistoryDao.updateLocalAudioPath(id, localFile.absolutePath, true)
                Result.success(localFile.absolutePath)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun isAudioCached(id: String): Boolean {
        val entity = oralHistoryDao.getById(id)
        return entity?.isAudioCachedLocally == true
    }

    override suspend fun insertAll(histories: List<OralHistoryEntity>) =
        oralHistoryDao.insertAll(histories)
}
