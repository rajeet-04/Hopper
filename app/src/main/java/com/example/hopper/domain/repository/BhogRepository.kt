package com.example.hopper.domain.repository

import com.example.hopper.data.local.db.entity.BhogPinEntity
import kotlinx.coroutines.flow.Flow

interface BhogRepository {
    fun getActivePinsByCategory(category: String): Flow<List<BhogPinEntity>>
    suspend fun submitBhogReport(pin: BhogPinEntity)
    suspend fun getUnsyncedPins(): List<BhogPinEntity>
    suspend fun markSynced(id: String)
    suspend fun deleteExpired()
}
