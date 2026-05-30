package com.example.hopper.domain.repository

import com.example.hopper.data.local.db.entity.LightTrailEntity
import kotlinx.coroutines.flow.Flow

interface LightTrailRepository {
    fun getTrailsByYear(year: Int): Flow<List<LightTrailEntity>>
    suspend fun insertAll(trails: List<LightTrailEntity>)
    suspend fun deleteAll()
}
