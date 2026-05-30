package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.LightTrailDao
import com.example.hopper.data.local.db.entity.LightTrailEntity
import com.example.hopper.domain.repository.LightTrailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LightTrailRepositoryImpl @Inject constructor(
    private val lightTrailDao: LightTrailDao
) : LightTrailRepository {
    override fun getTrailsByYear(year: Int): Flow<List<LightTrailEntity>> = lightTrailDao.getByYear(year)
    override suspend fun insertAll(trails: List<LightTrailEntity>) = lightTrailDao.insertAll(trails)
    override suspend fun deleteAll() = lightTrailDao.deleteAll()
}
