package com.example.hopper.domain.repository

import com.example.hopper.data.local.db.entity.HeritagePointEntity
import kotlinx.coroutines.flow.Flow

interface HeritageRepository {
    fun getByFestival(festival: String): Flow<List<HeritagePointEntity>>
    suspend fun getById(id: String): HeritagePointEntity?
    suspend fun insertAll(points: List<HeritagePointEntity>)
    suspend fun deleteAll()
}
