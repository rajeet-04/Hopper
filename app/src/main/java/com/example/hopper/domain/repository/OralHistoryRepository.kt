package com.example.hopper.domain.repository

import com.example.hopper.data.local.db.entity.OralHistoryEntity
import kotlinx.coroutines.flow.Flow
import java.io.File

interface OralHistoryRepository {
    fun getAll(): Flow<List<OralHistoryEntity>>
    fun getByPandalId(pandalId: String): Flow<List<OralHistoryEntity>>
    suspend fun getById(id: String): OralHistoryEntity?
    suspend fun downloadAudio(id: String, url: String, filesDir: File): Result<String>
    suspend fun isAudioCached(id: String): Boolean
    suspend fun insertAll(histories: List<OralHistoryEntity>)
}
