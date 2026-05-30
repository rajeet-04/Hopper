package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.HeritageDao
import com.example.hopper.data.local.db.entity.HeritagePointEntity
import com.example.hopper.domain.repository.HeritageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeritageRepositoryImpl @Inject constructor(
    private val heritageDao: HeritageDao
) : HeritageRepository {
    override fun getByFestival(festival: String): Flow<List<HeritagePointEntity>> =
        heritageDao.getByFestival(festival)

    override suspend fun getById(id: String): HeritagePointEntity? = heritageDao.getById(id)

    override suspend fun insertAll(points: List<HeritagePointEntity>) =
        heritageDao.insertAll(points)

    override suspend fun deleteAll() = heritageDao.deleteAll()
}
