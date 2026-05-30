package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.BhogDao
import com.example.hopper.data.local.db.entity.BhogPinEntity
import com.example.hopper.domain.repository.BhogRepository
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BhogRepositoryImpl @Inject constructor(
    private val bhogDao: BhogDao,
    private val clock: Clock
) : BhogRepository {

    override fun getActivePinsByCategory(category: String): Flow<List<BhogPinEntity>> =
        bhogDao.getActiveByCategory(category, clock.millis())

    override suspend fun submitBhogReport(pin: BhogPinEntity) =
        bhogDao.insert(pin)

    override suspend fun getUnsyncedPins(): List<BhogPinEntity> =
        bhogDao.getUnsyncedPins()

    override suspend fun markSynced(id: String) =
        bhogDao.markSynced(id)

    override suspend fun deleteExpired() =
        bhogDao.deleteExpired(clock.millis())
}
