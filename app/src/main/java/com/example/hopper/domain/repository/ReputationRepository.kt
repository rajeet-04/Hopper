package com.example.hopper.domain.repository

import com.example.hopper.data.local.db.entity.ReputationEntity
import kotlinx.coroutines.flow.Flow

interface ReputationRepository {
    suspend fun getReputation(deviceHash: String): ReputationEntity?
    suspend fun updateReputation(reputation: ReputationEntity)
    fun getLeaderboard(): Flow<List<ReputationEntity>>
    suspend fun calculateBadgeTier(totalReports: Int, accuracyScore: Double): String
    suspend fun calculateWeightMultiplier(accuracyScore: Double): Double
}
