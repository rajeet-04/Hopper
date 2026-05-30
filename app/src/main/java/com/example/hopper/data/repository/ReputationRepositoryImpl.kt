package com.example.hopper.data.repository

import com.example.hopper.data.local.db.dao.ReputationDao
import com.example.hopper.data.local.db.entity.ReputationEntity
import com.example.hopper.domain.repository.ReputationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReputationRepositoryImpl @Inject constructor(
    private val reputationDao: ReputationDao
) : ReputationRepository {

    override suspend fun getReputation(deviceHash: String): ReputationEntity? =
        reputationDao.getByDeviceHash(deviceHash)

    override suspend fun updateReputation(reputation: ReputationEntity) =
        reputationDao.upsert(reputation)

    override fun getLeaderboard(): Flow<List<ReputationEntity>> =
        reputationDao.getLeaderboard()

    override suspend fun calculateBadgeTier(totalReports: Int, accuracyScore: Double): String {
        return when {
            totalReports >= 50 && accuracyScore >= 0.8 -> "GOLD"
            totalReports >= 25 && accuracyScore >= 0.6 -> "SILVER"
            totalReports >= 10 -> "BRONZE"
            else -> "NONE"
        }
    }

    override suspend fun calculateWeightMultiplier(accuracyScore: Double): Double {
        return when {
            accuracyScore > 0.9 -> 2.0
            accuracyScore > 0.7 -> 1.5
            else -> 1.0
        }
    }
}
