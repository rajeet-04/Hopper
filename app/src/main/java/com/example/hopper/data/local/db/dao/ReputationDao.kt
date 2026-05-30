package com.example.hopper.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hopper.data.local.db.entity.ReputationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for reputation operations.
 */
@Dao
interface ReputationDao {

    @Query("SELECT * FROM reputations WHERE id = :deviceHash")
    suspend fun getByDeviceHash(deviceHash: String): ReputationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(reputation: ReputationEntity)

    @Query("SELECT * FROM reputations ORDER BY accuracy_score DESC LIMIT 50")
    fun getLeaderboard(): Flow<List<ReputationEntity>>
}
